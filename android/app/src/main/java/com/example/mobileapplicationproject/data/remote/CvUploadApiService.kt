
package com.example.mobileapplicationproject.data.remote

import com.example.mobileapplicationproject.data.remote.dto.CvUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CvUploadApiService {

    @Multipart
    @POST("api/v1/candidates/analyze-cv")
    suspend fun analyzeCv(
        @Part file: MultipartBody.Part
    ): CvUploadResponse
}
