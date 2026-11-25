package com.example.mobileapplicationproject.data.repository

import com.example.mobileapplicationproject.data.remote.RecommendationApiService
import com.example.mobileapplicationproject.data.remote.dto.RecommendationDto
import com.google.gson.JsonObject

class RecommendationRepository(
    private val api: RecommendationApiService
) {

    suspend fun getRecommendations(
        profile: JsonObject
    ): List<RecommendationDto> {

        val response = api.getRecommendations(
            candidateProfile = profile,
            topN = 10
        )

        if (response.status != "success") {
            throw IllegalStateException(
                "Unable to retrieve recommendations."
            )
        }

        return response.recommendations
    }
}
