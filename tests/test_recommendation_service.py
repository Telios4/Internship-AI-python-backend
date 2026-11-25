
import pytest

from src.internship_model import Internship
from src.recommendation_service import recommend_internships


@pytest.fixture
def candidate_profile():
    return {
        "education": [
            {
                "qualification": "Bachelor of Science",
                "field": "Computer Science",
                "institution": "University of Nairobi",
                "graduation_year": 2026,
            }
        ],
        "experience": [
            {
                "role": "Software Developer Intern",
                "employer": "ABC Technologies",
                "start_date": "Jan 2023",
                "end_date": "Jan 2024",
                "responsibilities": ["Developed Python applications"],
                "evidence": ["Developed Python applications"],
            }
        ],
        "skills": [
            {"skill": "python", "category": "technical"},
            {"skill": "sql", "category": "technical"},
            {"skill": "git", "category": "tools"},
        ],
    }


@pytest.fixture
def internship():
    return Internship(
        id="INT-001",
        title="Software Development Intern",
        organization="ABC Technologies",
        description="Assist with software development.",
        required_skills=["Python", "SQL", "Java"],
        preferred_skills=["Git", "Docker"],
        education_requirements=["Computer Science"],
        minimum_experience_months=12,
        eligibility_criteria=["Currently enrolled student"],
    )


def test_returns_recommendation_with_explanation(
    candidate_profile, internship
):
    results = recommend_internships(
        candidate_profile, [internship]
    )

    assert len(results) == 1
    assert results[0]["recommendation_level"] == "Good match"
    assert results[0]["reasons"]
    assert results[0]["improvement_areas"]


def test_explains_matched_required_skills(
    candidate_profile, internship
):
    results = recommend_internships(
        candidate_profile, [internship]
    )

    reasons = " ".join(results[0]["reasons"]).lower()

    assert "python" in reasons
    assert "sql" in reasons


def test_identifies_missing_required_skills(
    candidate_profile, internship
):
    results = recommend_internships(
        candidate_profile, [internship]
    )

    improvements = " ".join(
        results[0]["improvement_areas"]
    ).lower()

    assert "java" in improvements


def test_includes_eligibility_note(
    candidate_profile, internship
):
    results = recommend_internships(
        candidate_profile, [internship]
    )

    assert "manually" in results[0]["eligibility_note"].lower()


def test_ranks_recommendations_by_score(candidate_profile):
    strong = Internship(
        id="INT-001",
        title="Python Developer Intern",
        organization="Company A",
        description="Python and SQL development.",
        required_skills=["Python", "SQL"],
        education_requirements=["Computer Science"],
    )

    weak = Internship(
        id="INT-002",
        title="Java Developer Intern",
        organization="Company B",
        description="Java development.",
        required_skills=["Java", "C++"],
        education_requirements=["Computer Science"],
    )

    results = recommend_internships(
        candidate_profile, [weak, strong]
    )

    assert results[0]["internship_id"] == "INT-001"
    assert results[1]["internship_id"] == "INT-002"


def test_limits_number_of_recommendations(candidate_profile):
    internships = [
        Internship(
            id=f"INT-{number}",
            title=f"Internship {number}",
            organization=f"Company {number}",
            description="A sample internship.",
            required_skills=["Python", "SQL"],
            preferred_skills=["Git"],
            education_requirements=["Computer Science"],
            minimum_experience_months=0,
        )
        for number in range(1, 4)
    ]

    results = recommend_internships(
        candidate_profile, internships, top_n=2
    )

    assert len(results) == 2

def test_zero_top_n_returns_empty_list(
    candidate_profile, internship
):
    results = recommend_internships(
        candidate_profile, [internship], top_n=0
    )

    assert results == []


def test_rejects_negative_top_n(candidate_profile, internship):
    with pytest.raises(ValueError):
        recommend_internships(
            candidate_profile, [internship], top_n=-1
        )


def test_rejects_non_integer_top_n(candidate_profile, internship):
    with pytest.raises(TypeError):
        recommend_internships(
            candidate_profile, [internship], top_n=2.5
        )


def test_empty_internship_list_returns_empty_list(candidate_profile):
    assert recommend_internships(candidate_profile, []) == []
def test_excludes_internships_without_matching_criteria(
    candidate_profile
):
    internship = Internship(
        id="INT-NO-DATA",
        title="General Internship",
        organization="Company A",
        description="An internship without structured requirements.",
    )

    results = recommend_internships(
        candidate_profile,
        [internship]
    )

    assert results == []
def test_recommends_internships_with_matching_criteria(
    candidate_profile
):
    internship = Internship(
        id="INT-VALID",
        title="Python Developer Intern",
        organization="Company B",
        description="Assist with Python development.",
        required_skills=["Python", "SQL"],
        preferred_skills=["Git"],
        education_requirements=["Computer Science"],
    )

    results = recommend_internships(
        candidate_profile,
        [internship]
    )

    assert len(results) == 1
    assert results[0]["internship_id"] == "INT-VALID"
    assert results[0]["overall_score"] == 100.0
def test_filters_incomplete_internships_before_ranking(
    candidate_profile
):
    incomplete = Internship(
        id="INT-INCOMPLETE",
        title="General Internship",
        organization="Company A",
        description="No structured requirements.",
    )

    complete = Internship(
        id="INT-COMPLETE",
        title="Software Developer Intern",
        organization="Company B",
        description="Software development opportunity.",
        required_skills=["Python", "Java"],
        preferred_skills=["Git", "Docker"],
        education_requirements=["Computer Science"],
    )

    results = recommend_internships(
        candidate_profile,
        [incomplete, complete],
        top_n=1
    )

    assert len(results) == 1
    assert results[0]["internship_id"] == "INT-COMPLETE"
    assert results[0]["overall_score"] < 100
