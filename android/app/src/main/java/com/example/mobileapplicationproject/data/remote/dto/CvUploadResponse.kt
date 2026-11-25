
package com.example.mobileapplicationproject.data.remote.dto

import com.google.gson.JsonObject

data class CvUploadResponse(
    val status: String,
    val message: String,
    val filename: String,
    val profile: JsonObject
)
