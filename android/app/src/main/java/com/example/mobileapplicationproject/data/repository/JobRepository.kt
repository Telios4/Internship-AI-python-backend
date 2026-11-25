
package com.example.mobileapplicationproject.data.repository

import com.example.mobileapplicationproject.data.model.Job

interface JobRepository {
    suspend fun getJobs(): List<Job>
}
