
package com.example.mobileapplicationproject.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileapplicationproject.data.remote.CvUploadApiService
import com.example.mobileapplicationproject.data.remote.RecommendationApiService
import com.example.mobileapplicationproject.data.remote.dto.RecommendationDto
import com.example.mobileapplicationproject.data.repository.RemoteCvRepository
import com.example.mobileapplicationproject.data.repository.RecommendationRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val cvRepository = RemoteCvRepository(
        api = retrofit.create(
            CvUploadApiService::class.java
        ),
        resolver = application.contentResolver
    )

    private val recommendationRepository =
        RecommendationRepository(
            retrofit.create(
                RecommendationApiService::class.java
            )
        )

    var selectedCvUri by mutableStateOf<Uri?>(null)
        private set

    var selectedCvName by mutableStateOf<String?>(null)
        private set

    var selectedCvSize by mutableStateOf<Long?>(null)
        private set

    var isUploading by mutableStateOf(false)
        private set

    var uploadMessage by mutableStateOf<String?>(null)
        private set

    var uploadSucceeded by mutableStateOf(false)
        private set

    var candidateProfile by mutableStateOf<JsonObject?>(null)
        private set

    var recommendations by mutableStateOf<List<RecommendationDto>>(
        emptyList()
    )
        private set

    var isLoadingRecommendations by mutableStateOf(false)
        private set

    var recommendationError by mutableStateOf<String?>(null)
        private set

    fun selectCv(
        uri: Uri,
        name: String,
        size: Long?
    ) {
        if (isUploading || isLoadingRecommendations) return

        selectedCvUri = uri
        selectedCvName = name
        selectedCvSize = size
        uploadMessage = null
        uploadSucceeded = false
        candidateProfile = null
        recommendations = emptyList()
        recommendationError = null
    }

    fun removeCv() {
        if (isUploading || isLoadingRecommendations) return

        selectedCvUri = null
        selectedCvName = null
        selectedCvSize = null
        uploadMessage = null
        uploadSucceeded = false
        candidateProfile = null
        recommendations = emptyList()
        recommendationError = null
    }

    fun uploadCv(studentId: Int = 1) {
        val uri = selectedCvUri ?: run {
            uploadMessage = "Please select a PDF CV."
            return
        }

        val filename = selectedCvName ?: "cv.pdf"

        if (!filename.endsWith(".pdf", ignoreCase = true)) {
            uploadMessage = "Only PDF files are supported."
            return
        }

        if (isUploading || isLoadingRecommendations) return

        viewModelScope.launch {
            isUploading = true
            uploadSucceeded = false
            uploadMessage = null
            candidateProfile = null
            recommendations = emptyList()
            recommendationError = null

            try {
                val response = withContext(Dispatchers.IO) {
                    cvRepository.analyzeCv(
                        uri = uri,
                        filename = filename
                    )
                }

                if (response.status != "success") {
                    uploadMessage = "CV analysis failed."
                    return@launch
                }

                candidateProfile = response.profile
                uploadSucceeded = true
                uploadMessage = response.message

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: Exception) {
                uploadMessage = exception.message
                    ?: "Unable to analyze CV."
                return@launch

            } finally {
                isUploading = false
            }

            loadRecommendations()
        }
    }

    fun loadRecommendations() {
        val profile = candidateProfile ?: run {
            recommendationError =
                "Analyze your CV before requesting recommendations."
            return
        }

        if (isLoadingRecommendations) return

        viewModelScope.launch {
            isLoadingRecommendations = true
            recommendationError = null

            try {
                recommendations =
                    recommendationRepository.getRecommendations(
                        profile
                    )

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: Exception) {
                recommendationError = exception.message
                    ?: "Unable to load recommendations."

            } finally {
                isLoadingRecommendations = false
            }
        }
    }
}
