
package com.example.mobileapplicationproject.data.model

data class Job(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val type: String,
    val salary: String,
    val matchScore: Int? = null,
    val posted: String = "Date not available",
    val skills: List<String>,
    val description: String = "",
    val preferredSkills: List<String> = emptyList(),
    val applicationDeadline: String? = null,
    val applicationUrl: String? = null,
    val isSaved: Boolean = false,
    val isApplied: Boolean = false
)
