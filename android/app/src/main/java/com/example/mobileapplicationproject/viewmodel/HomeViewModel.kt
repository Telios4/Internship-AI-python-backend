
package com.example.mobileapplicationproject.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileapplicationproject.data.model.Job
import com.example.mobileapplicationproject.data.remote.RetrofitClient
import com.example.mobileapplicationproject.data.remote.dto.RecommendationDto
import com.example.mobileapplicationproject.data.repository.JobRepository
import com.example.mobileapplicationproject.data.repository.RemoteJobRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class HomeViewModel(
    private val repository: JobRepository =
        RemoteJobRepository(RetrofitClient.apiService)
) : ViewModel() {

    var jobs by mutableStateOf<List<Job>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var recommendations by mutableStateOf<List<RecommendationDto>>(
        emptyList()
    )
        private set

    init {
        loadJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val loadedJobs = repository.getJobs()
                val previousJobs = jobs.associateBy { it.id }
                val scores = recommendations.associateBy {
                    it.internshipId
                }

                jobs = loadedJobs.map { newJob ->
                    val previous = previousJobs[newJob.id]
                    val recommendation = scores[newJob.id]

                    newJob.copy(
                        isSaved = previous?.isSaved ?: newJob.isSaved,
                        isApplied = previous?.isApplied ?: newJob.isApplied,
                        matchScore = recommendation
                            ?.overallScore
                            ?.roundToInt()
                    )
                }

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: Exception) {
                errorMessage = exception.message
                    ?: "Unable to load opportunities."

            } finally {
                isLoading = false
            }
        }
    }

    fun updateRecommendations(
        newRecommendations: List<RecommendationDto>
    ) {
        recommendations = newRecommendations

        val scores = newRecommendations.associateBy {
            it.internshipId
        }

        jobs = jobs.map { job ->
            val recommendation = scores[job.id]

            job.copy(
                matchScore = recommendation
                    ?.overallScore
                    ?.roundToInt()
            )
        }
    }

    fun getRecommendation(
        internshipId: String
    ): RecommendationDto? {
        return recommendations.find {
            it.internshipId == internshipId
        }
    }

    fun retry() {
        loadJobs()
    }

    fun updateSearch(query: String) {
        searchQuery = query
    }

    fun toggleSaved(id: String) {
        jobs = jobs.map { job ->
            if (job.id == id) {
                job.copy(isSaved = !job.isSaved)
            } else {
                job
            }
        }
    }

    val filteredJobs: List<Job>
        get() = jobs.filter { job ->
            job.title.contains(
                searchQuery,
                ignoreCase = true
            ) ||
                    job.company.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||
                    job.skills.any { skill ->
                        skill.contains(
                            searchQuery,
                            ignoreCase = true
                        )
                    }
        }

    val savedJobs: List<Job>
        get() = jobs.filter { it.isSaved }

    val appliedJobs: List<Job>
        get() = jobs.filter { it.isApplied }
}
