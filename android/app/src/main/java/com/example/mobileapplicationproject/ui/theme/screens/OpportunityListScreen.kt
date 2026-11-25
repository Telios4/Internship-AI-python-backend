
package com.example.mobileapplicationproject.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobileapplicationproject.components.JobCard
import com.example.mobileapplicationproject.data.model.Job

private val Background = Color(0xFFF8F9FF)
private val Indigo = Color(0xFF273469)
private val Muted = Color(0xFF69748E)

@Composable
fun OpportunityListScreen(
    title: String,
    jobs: List<Job>,
    onJobClick: (String) -> Unit,
    onSaveClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Indigo
            )
        }

        if (jobs.isEmpty()) {
            item {
                Text(
                    text = when (title) {
                        "Saved Opportunities" ->
                            "You haven't saved any opportunities yet."
                        "My Applications" ->
                            "You haven't marked any opportunities as applied yet."
                        else ->
                            "No opportunities to display."
                    },
                    color = Muted,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            items(
                items = jobs,
                key = { it.id }
            ) { job ->
                JobCard(
                    job = job,
                    onBreakdownClick = {
                        onJobClick(job.id)
                    },
                    onSaveClick = {
                        onSaveClick(job.id)
                    }
                )
            }
        }
    }
}
