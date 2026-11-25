package com.example.mobileapplicationproject.data.remote

import com.example.mobileapplicationproject.data.remote.dto.RecommendationResponse
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RecommendationApiService {

    @POST("api/v1/recommendations")
    suspend fun getRecommendations(
        @Body candidateProfile: JsonObject,
        @Query("top_n") topN: Int = 10
    ): RecommendationResponse
}
