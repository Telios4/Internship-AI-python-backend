
package com.example.mobileapplicationproject.data.repository

import android.net.Uri
import com.example.mobileapplicationproject.data.remote.dto.CvUploadResponse

interface CvRepository {
    suspend fun uploadCv(
        studentId: Int,
        uri: Uri
    ): CvUploadResponse
}
