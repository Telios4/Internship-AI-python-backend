
package com.example.mobileapplicationproject.data.repository

import com.example.mobileapplicationproject.data.model.Job

class MockJobRepository(
    private val sampleJobs: List<Job>
) : JobRepository {

    override suspend fun getJobs(): List<Job> {

        return sampleJobs
    }
}
