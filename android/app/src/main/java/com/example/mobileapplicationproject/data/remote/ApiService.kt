
package com.example.mobileapplicationproject.data.remote

import com.example.mobileapplicationproject.data.remote.dto.JobDto
import com.example.mobileapplicationproject.data.remote.dto.InternshipListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("api/v1/internships")
    suspend fun getJobs(): InternshipListResponse

    @GET("api/v1/internships/{internship_id}")
    suspend fun getJob(
        @Path("internship_id") id: String
    ): JobDto
}
