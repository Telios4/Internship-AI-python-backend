
package com.example.mobileapplicationproject.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecommendationResponse(
    val status: String,
    val count: Int,
    val recommendations: List<RecommendationDto>
)

data class RecommendationDto(
    @SerializedName("internship_id")
    val internshipId: String,

    @SerializedName("internship_title")
    val internshipTitle: String,

    val organization: String,

    @SerializedName("overall_score")
    val overallScore: Double,

    @SerializedName("score_breakdown")
    val scoreBreakdown: ScoreBreakdownDto,

    @SerializedName("matched_required_skills")
    val matchedRequiredSkills: List<String>,

    @SerializedName("missing_required_skills")
    val missingRequiredSkills: List<String>,

    @SerializedName("matched_preferred_skills")
    val matchedPreferredSkills: List<String>,

    @SerializedName("missing_preferred_skills")
    val missingPreferredSkills: List<String>,

    @SerializedName("detected_experience_months")
    val detectedExperienceMonths: Int,

    @SerializedName("recommendation_level")
    val recommendationLevel: String,

    val reasons: List<String>,

    @SerializedName("improvement_areas")
    val improvementAreas: List<String>,

    @SerializedName("eligibility_note")
    val eligibilityNote: String
)

data class ScoreBreakdownDto(
    @SerializedName("required_skills")
    val requiredSkills: Double,

    @SerializedName("preferred_skills")
    val preferredSkills: Double,

    val education: Double,
    val experience: Double
)
