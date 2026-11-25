
package com.example.mobileapplicationproject.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobileapplicationproject.data.model.Job

private val Blue = Color(0xFF4F6BED)
private val Lavender = Color(0xFFEDE9FE)
private val Indigo = Color(0xFF273469)
private val Muted = Color(0xFF69748E)

@Composable
fun JobCard(
    job: Job,
    onBreakdownClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Lavender,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = job.company.firstOrNull()
                            ?.uppercase() ?: "?",
                        modifier = Modifier.padding(14.dp),
                        color = Indigo,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = job.title,
                        fontWeight = FontWeight.Bold,
                        color = Indigo,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = job.company,
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = if (job.isSaved) {
                            Icons.Filled.Bookmark
                        } else {
                            Icons.Outlined.BookmarkBorder
                        },
                        contentDescription = if (job.isSaved) {
                            "Remove saved opportunity"
                        } else {
                            "Save opportunity"
                        },
                        tint = Blue
                    )
                }
            }

            Text(
                text = "${job.type}  •  ${job.location}  •  ${job.posted}",
                color = Muted,
                style = MaterialTheme.typography.bodySmall
            )

            // Skills
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                job.skills.take(3).forEach { skill ->
                    Surface(
                        color = Lavender,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = skill,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            color = Indigo,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.matchScore?.let {
                        "$it% match (demo)"
                    } ?: "Not analyzed",
                    color = if (job.matchScore == null) {
                        Muted
                    } else {
                        Blue
                    },
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = job.salary,
                    color = Indigo,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Button(
                onClick = onBreakdownClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View details")
            }

            if (job.isApplied) {
                Text(
                    text = "✓ Applied",
                    color = Blue,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
