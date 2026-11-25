
package com.example.mobileapplicationproject.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.example.mobileapplicationproject.data.remote.CvUploadApiService
import com.example.mobileapplicationproject.data.remote.dto.CvUploadResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RemoteCvRepository(
    private val api: CvUploadApiService,
    private val resolver: ContentResolver
) {

    suspend fun analyzeCv(
        uri: Uri,
        filename: String
    ): CvUploadResponse {

        val bytes = resolver.openInputStream(uri)?.use {
            it.readBytes()
        } ?: throw IllegalStateException(
            "Unable to open the selected CV."
        )

        if (bytes.isEmpty()) {
            throw IllegalArgumentException(
                "The selected CV is empty."
            )
        }

        if (bytes.size > 5 * 1024 * 1024) {
            throw IllegalArgumentException(
                "The CV must be 5 MB or smaller."
            )
        }

        val requestBody = RequestBody.create(
            MediaType.get("application/pdf"),
            bytes
        )

        val filePart = MultipartBody.Part.createFormData(
            "file",
            filename,
            requestBody
        )

        return api.analyzeCv(filePart)
    }
}
