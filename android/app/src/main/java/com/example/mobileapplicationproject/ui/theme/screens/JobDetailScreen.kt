
package com.example.mobileapplicationproject.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobileapplicationproject.data.model.Job
import com.example.mobileapplicationproject.data.remote.dto.RecommendationDto
import kotlin.math.roundToInt

private val Blue = Color(0xFF4F6BED)
private val Indigo = Color(0xFF273469)
private val Lavender = Color(0xFFEDE9FE)
private val PageBg = Color(0xFFF8F9FF)
private val Muted = Color(0xFF69748E)
private val Green = Color(0xFF16805D)
private val SoftGreen = Color(0xFFE6F5ED)
private val SoftOrange = Color(0xFFFFF3E1)

@Composable
fun JobDetailScreen(
    job: Job?,
    onBack: () -> Unit,
    recommendation: RecommendationDto? = null
) {
    var showCoach by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .verticalScroll(rememberScrollState())
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Lavender)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Blue
                )
                Spacer(Modifier.width(8.dp))
                Text("Back", color = Blue)
            }

            Text(
                text = "OPPORTUNITY DETAILS",
                color = Blue,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = job?.title ?: "Opportunity not found",
                color = Indigo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (job != null) {
                Text(
                    text = "${job.company} • ${job.location}",
                    color = Muted
                )
            }
        }

        if (job == null) {
            Text(
                text = "This opportunity could not be found.",
                modifier = Modifier.padding(20.dp),
                color = Muted
            )
            return@Column
        }

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            DetailCard("Your AI Match") {

                if (recommendation != null) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "${recommendation.overallScore.roundToInt()}%",
                                color = Blue,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = recommendation.recommendationLevel,
                                color = Indigo,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Blue,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = {
                            (recommendation.overallScore / 100.0)
                                .toFloat()
                                .coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = Blue,
                        trackColor = Lavender
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Calculated from the qualifications " +
                                "extracted from your CV.",
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )

                } else {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Blue
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Not analyzed",
                            color = Indigo,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Upload and analyze your CV in " +
                                "My Profile to calculate your match " +
                                "score for this internship.",
                        color = Muted
                    )
                }
            }

            DetailCard("Opportunity Overview") {
                DetailRow("Company", job.company)
                DetailRow("Location", job.location)
                DetailRow("Work type", job.type)
                DetailRow("Compensation", job.salary)
                DetailRow("Internship ID", job.id)
            }

            DetailCard("Skills Listed for This Role") {
                if (job.skills.isEmpty()) {
                    Text(
                        text = "No skills listed.",
                        color = Muted
                    )
                } else {
                    job.skills.forEach { skill ->
                        Surface(
                            color = Lavender,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = skill,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 9.dp
                                ),
                                color = Indigo
                            )
                        }
                    }
                }
            }

            if (recommendation != null) {

                DetailCard("Match Score Breakdown") {

                    ScoreRow(
                        "Required skills",
                        recommendation.scoreBreakdown.requiredSkills
                    )

                    ScoreRow(
                        "Preferred skills",
                        recommendation.scoreBreakdown.preferredSkills
                    )

                    ScoreRow(
                        "Education",
                        recommendation.scoreBreakdown.education
                    )

                    ScoreRow(
                        "Experience",
                        recommendation.scoreBreakdown.experience
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "These component scores are " +
                                "calculated by the backend matching engine.",
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                DetailCard("Your Skill Match") {

                    SkillSection(
                        title = "Matched required skills",
                        skills = recommendation.matchedRequiredSkills,
                        background = SoftGreen
                    )

                    Spacer(Modifier.height(14.dp))

                    SkillSection(
                        title = "Missing required skills",
                        skills = recommendation.missingRequiredSkills,
                        background = SoftOrange
                    )

                    Spacer(Modifier.height(14.dp))

                    SkillSection(
                        title = "Matched preferred skills",
                        skills = recommendation.matchedPreferredSkills,
                        background = SoftGreen
                    )

                    Spacer(Modifier.height(14.dp))

                    SkillSection(
                        title = "Missing preferred skills",
                        skills = recommendation.missingPreferredSkills,
                        background = Lavender
                    )
                }

                DetailCard("Eligibility Review") {

                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Blue
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = recommendation.eligibilityNote,
                        color = Indigo
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "A match score does not guarantee " +
                                "that you meet the internship's " +
                                "eligibility requirements.",
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                DetailCard("Why This Role Matches") {

                    if (recommendation.reasons.isEmpty()) {
                        Text(
                            text = "No matching reasons were identified.",
                            color = Muted
                        )
                    } else {
                        recommendation.reasons.forEach { reason ->
                            ExplanationRow(
                                text = reason,
                                positive = true
                            )
                        }
                    }
                }

                DetailCard("Areas for Improvement") {

                    if (recommendation.improvementAreas.isEmpty()) {
                        Text(
                            text = "No specific improvement areas " +
                                    "were identified.",
                            color = Muted
                        )
                    } else {
                        recommendation.improvementAreas.forEach {
                            ExplanationRow(
                                text = it,
                                positive = false
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { showCoach = !showCoach },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = if (showCoach) {
                        "Hide Application Tips"
                    } else {
                        "Explore Application Coach"
                    }
                )
            }

            if (showCoach) {
                DetailCard("Application Coach") {

                    CoachingTip(
                        "1. Review the requirements",
                        "Compare the internship's requirements " +
                                "with your qualifications."
                    )

                    CoachingTip(
                        "2. Tailor your CV",
                        "Highlight relevant skills and experience " +
                                "you genuinely possess."
                    )

                    CoachingTip(
                        "3. Demonstrate your skills",
                        "Use projects, achievements, and examples " +
                                "to support your application."
                    )

                    CoachingTip(
                        "4. Prepare for interviews",
                        "Practice explaining your projects " +
                                "and relevant experience."
                    )

                    Text(
                        text = "These are general preparation tips.",
                        color = Muted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = Indigo,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Muted,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = Indigo,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1.3f)
        )
    }
}

@Composable
private fun ScoreRow(
    title: String,
    score: Double
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Indigo
            )

            Text(
                text = "${score.roundToInt()}%",
                color = Blue,
                fontWeight = FontWeight.Bold
            )
        }

        LinearProgressIndicator(
            progress = {
                (score / 100.0)
                    .toFloat()
                    .coerceIn(0f, 1f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp),
            color = Blue,
            trackColor = Lavender
        )
    }
}

@Composable
private fun SkillSection(
    title: String,
    skills: List<String>,
    background: Color
) {
    Text(
        text = title,
        color = Indigo,
        fontWeight = FontWeight.SemiBold
    )

    if (skills.isEmpty()) {
        Text(
            text = "None identified",
            color = Muted,
            style = MaterialTheme.typography.bodySmall
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                skills.forEach { skill ->
                    Surface(
                        color = background,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = skill,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            color = Indigo
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExplanationRow(
    text: String,
    positive: Boolean
) {
    Row(
        modifier = Modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (positive) {
                Icons.Outlined.CheckCircle
            } else {
                Icons.Outlined.Info
            },
            contentDescription = null,
            tint = if (positive) Green else Blue,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = text,
            color = Muted,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CoachingTip(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Text(
            text = title,
            color = Blue,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = description,
            color = Muted
        )
    }
}
