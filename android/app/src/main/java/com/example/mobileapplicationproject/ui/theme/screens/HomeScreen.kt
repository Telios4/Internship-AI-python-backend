
package com.example.mobileapplicationproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobileapplicationproject.components.JobCard
import com.example.mobileapplicationproject.viewmodel.HomeViewModel
import java.util.Calendar

private val Blue = Color(0xFF4F6BED)
private val Indigo = Color(0xFF273469)
private val Background = Color(0xFFF8F9FF)
private val Muted = Color(0xFF69748E)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onJobClick: (String) -> Unit,
    onSavedClick: () -> Unit = {},
    onAppliedClick: () -> Unit = {},
    studentName: String = "Alex",
    onProfileClick: () -> Unit = {}
) {
    val greeting = when (
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    val firstName = studentName.trim()
        .substringBefore(" ")
        .ifBlank { "Student" }

    val filteredJobs = viewModel.filteredJobs
        .sortedByDescending { it.matchScore ?: -1 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(
            horizontal = 18.dp,
            vertical = 22.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "YOUR CAREER COMPANION",
                    color = Blue,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "$greeting, $firstName 👋",
                    color = Indigo,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Find your next step, at your own pace.",
                    color = Muted
                )
            }
        }

        item {
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::updateSearch,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search jobs, companies or skills")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Blue,
                    unfocusedBorderColor = Color(0xFFDCE2F4)
                )
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Discover opportunities",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Indigo
                )

                Text(
                    text = "Explore available roles and save the ones you like.",
                    color = Muted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        when {
            viewModel.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Blue
                        )
                    }
                }
            }

            viewModel.errorMessage != null -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Could not load opportunities",
                                fontWeight = FontWeight.Bold,
                                color = Indigo
                            )

                            Text(
                                text = viewModel.errorMessage
                                    ?: "An unexpected error occurred.",
                                color = Muted
                            )

                            Button(
                                onClick = viewModel::retry,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Blue
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }

            filteredJobs.isEmpty() -> {
                item {
                    Text(
                        text = if (viewModel.searchQuery.isBlank()) {
                            "No opportunities available yet."
                        } else {
                            "No opportunities match your search."
                        },
                        color = Muted,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            else -> {
                item {
                    Text(
                        text = "${filteredJobs.size} roles available",
                        color = Blue,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                items(
                    items = filteredJobs,
                    key = { it.id }
                ) { job ->
                    JobCard(
                        job = job,
                        onBreakdownClick = {
                            onJobClick(job.id)
                        },
                        onSaveClick = {
                            viewModel.toggleSaved(job.id)
                        }
                    )
                }
            }
        }
    }
}
