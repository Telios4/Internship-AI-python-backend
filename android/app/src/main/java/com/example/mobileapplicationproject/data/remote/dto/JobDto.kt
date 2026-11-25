
package com.example.mobileapplicationproject.data.remote.dto

import com.google.gson.annotations.SerializedName

data class JobDto(
    val id: String,
    val title: String,
    val organization: String,
    val description: String = "",

    @SerializedName("required_skills")
    val requiredSkills: List<String> = emptyList(),

    @SerializedName("preferred_skills")
    val preferredSkills: List<String> = emptyList(),

    @SerializedName("education_requirements")
    val educationRequirements: List<String> = emptyList(),

    @SerializedName("minimum_experience_months")
    val minimumExperienceMonths: Int = 0,

    @SerializedName("eligibility_criteria")
    val eligibilityCriteria: List<String> = emptyList(),

    val location: String = "",

    @SerializedName("work_arrangement")
    val workArrangement: String = "",

    @SerializedName("application_deadline")
    val applicationDeadline: String? = null,

    @SerializedName("application_url")
    val applicationUrl: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true
)

data class InternshipListResponse(
    val status: String,
    val count: Int,
    val internships: List<JobDto>
)
