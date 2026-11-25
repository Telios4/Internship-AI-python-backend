
package com.example.mobileapplicationproject.data.repository

import com.example.mobileapplicationproject.data.model.Job
import com.example.mobileapplicationproject.data.remote.ApiService

class RemoteJobRepository(
    private val apiService: ApiService
) : JobRepository {

    override suspend fun getJobs(): List<Job> {

        val response = apiService.getJobs()

        if (response.status != "success") {
            throw IllegalStateException(
                "Backend did not return a successful response."
            )
        }

        return response.internships
            .filter { it.isActive }
            .map { dto ->

                Job(
                    id = dto.id,
                    title = dto.title,
                    company = dto.organization,
                    location = dto.location,
                    type = dto.workArrangement
                        .replaceFirstChar {
                            it.uppercase()
                        },
                    salary = "Not specified",
                    matchScore = null,
                    posted = "Date not available",
                    skills = dto.requiredSkills,
                    description = dto.description,
                    preferredSkills = dto.preferredSkills,
                    applicationDeadline =
                        dto.applicationDeadline,
                    applicationUrl = dto.applicationUrl
                )
            }
    }
}
