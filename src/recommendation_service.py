
from src.matching_engine import rank_internships


def _get_recommendation_level(score: float) -> str:
    if score >= 80:
        return "Strong match"
    if score >= 60:
        return "Good match"
    return "Developing match"


def _build_explanation(result: dict, internship) -> dict:
    breakdown = result["score_breakdown"]

    reasons = []
    improvement_areas = []

    matched_required = result["matched_required_skills"]
    missing_required = result["missing_required_skills"]
    matched_preferred = result["matched_preferred_skills"]
    missing_preferred = result["missing_preferred_skills"]

    # Required skills
    if internship.required_skills:
        if matched_required:
            reasons.append(
                "You demonstrate these required skills: "
                + ", ".join(matched_required) + "."
            )

        if not missing_required:
            reasons.append(
                "You have all the required skills listed "
                "for this internship."
            )
        else:
            improvement_areas.append(
                "Develop these missing required skills: "
                + ", ".join(missing_required) + "."
            )
    else:
        reasons.append(
            "The employer has not specified required skills."
        )

    # Preferred skills
    if internship.preferred_skills:
        if matched_preferred:
            reasons.append(
                "Your profile also includes preferred skills: "
                + ", ".join(matched_preferred) + "."
            )

        if missing_preferred:
            improvement_areas.append(
                "Consider developing these additional "
                "preferred skills: "
                + ", ".join(missing_preferred) + "."
            )
    else:
        reasons.append(
            "The employer has not specified preferred skills."
        )

    # Education requirements
    if internship.education_requirements:
        if breakdown["education"] >= 75:
            reasons.append(
                "Your educational background is relevant "
                "to the stated requirements."
            )
        elif breakdown["education"] > 0:
            reasons.append(
                "Your educational background has some overlap "
                "with the stated requirements."
            )
        else:
            improvement_areas.append(
                "Your CV does not currently demonstrate a clear match "
                "with the stated education requirements. "
                "Review the requirements and provide relevant "
                "evidence if applicable."
            )
    else:
        reasons.append(
            "The employer has not specified education requirements."
        )

    # Experience requirements
    if internship.minimum_experience_months > 0:
        if breakdown["experience"] >= 75:
            reasons.append(
                "Your practical experience meets or closely "
                "matches the experience requirement."
            )
        elif breakdown["experience"] > 0:
            reasons.append(
                "Your practical experience partially meets "
                "the stated requirement."
            )
        else:
            improvement_areas.append(
                "Gain or document relevant practical "
                "experience where possible."
            )
    else:
        reasons.append(
            "No positive minimum experience requirement "
            "is recorded for this internship."
        )

    if not reasons:
        reasons.append(
            "This opportunity has been included based on "
            "the available profile information."
        )

    return {
        "recommendation_level": _get_recommendation_level(
            result["overall_score"]
        ),
        "reasons": reasons,
        "improvement_areas": improvement_areas,
        "eligibility_note": (
            "Review the internship eligibility criteria manually "
            "before applying."
        ),
    }


def _has_matching_criteria(internship) -> bool:
    """
    Check whether an internship contains meaningful
    structured requirements for personalized matching.
    """
    return bool(
        internship.required_skills
        or internship.preferred_skills
        or internship.education_requirements
        or internship.minimum_experience_months > 0
    )


def recommend_internships(
    candidate_profile: dict,
    internships: list,
    top_n: int | None = None,
) -> list[dict]:
    """
    Rank internships and attach human-readable explanations.

    Only active internships with meaningful matching
    criteria are considered for personalized recommendations.

    Internships without matching criteria remain available
    through the internship listing endpoint.
    """
    if top_n is not None:
        if isinstance(top_n, bool) or not isinstance(top_n, int):
            raise TypeError(
                "top_n must be an integer or None."
            )

        if top_n < 0:
            raise ValueError(
                "top_n cannot be negative."
            )

    # Exclude internships without structured matching criteria.
    scorable_internships = [
        internship
        for internship in internships
        if _has_matching_criteria(internship)
    ]

    # Rank internships using the existing matching engine.
    results = rank_internships(
        candidate_profile,
        scorable_internships
    )

    # Apply the requested recommendation limit.
    if top_n is not None:
        results = results[:top_n]

    # Map internship IDs to their original records.
    internship_lookup = {
        internship.id: internship
        for internship in scorable_internships
    }

    recommendations = []

    for result in results:
        internship = internship_lookup[
            result["internship_id"]
        ]

        explanation = _build_explanation(
            result,
            internship
        )

        recommendations.append({
            **result,
            **explanation,
        })

    return recommendations
