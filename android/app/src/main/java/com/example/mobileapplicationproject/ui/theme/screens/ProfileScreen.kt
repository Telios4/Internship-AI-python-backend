
package com.example.mobileapplicationproject.ui.theme.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobileapplicationproject.viewmodel.ProfileViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject

private val Blue = Color(0xFF4F6BED)
private val Lavender = Color(0xFFEDE9FE)
private val Background = Color(0xFFF8F9FF)
private val Indigo = Color(0xFF273469)
private val Muted = Color(0xFF69748E)

@Composable
fun ProfileScreen(
    studentName: String = "Alex",
    onStudentNameChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current

    var course by rememberSaveable {
        mutableStateOf("Computer Science")
    }

    var university by rememberSaveable {
        mutableStateOf("Strathmore University")
    }

    var interests by rememberSaveable {
        mutableStateOf("Software Development, Data Analytics")
    }

    var skills by rememberSaveable {
        mutableStateOf("Kotlin, Python, SQL, Git")
    }

    var selectionError by remember {
        mutableStateOf<String?>(null)
    }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        if (uri != null) {
            try {
                var filename = "cv.pdf"
                var size: Long? = null

                context.contentResolver.query(
                    uri,
                    arrayOf(
                        OpenableColumns.DISPLAY_NAME,
                        OpenableColumns.SIZE
                    ),
                    null,
                    null,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                        )
                        val sizeIndex = cursor.getColumnIndex(
                            OpenableColumns.SIZE
                        )

                        if (nameIndex >= 0) {
                            filename = cursor.getString(nameIndex)
                                ?: filename
                        }

                        if (sizeIndex >= 0 &&
                            !cursor.isNull(sizeIndex)
                        ) {
                            size = cursor.getLong(sizeIndex)
                        }
                    }
                }

                when {
                    !filename.endsWith(
                        ".pdf",
                        ignoreCase = true
                    ) -> {
                        selectionError =
                            "Please choose a PDF file."
                    }

                    size != null &&
                            size!! > 5L * 1024L * 1024L -> {
                        selectionError =
                            "CV must be 5 MB or smaller."
                    }

                    else -> {
                        selectionError = null
                        profileViewModel.selectCv(
                            uri = uri,
                            name = filename,
                            size = size
                        )
                    }
                }
            } catch (exception: Exception) {
                selectionError =
                    "Could not read the selected file."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        Text(
            text = "My Profile",
            color = Indigo,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Build your profile and discover your strengths.",
            color = Muted
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Lavender
            ),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "STUDENT INFORMATION",
                    color = Blue,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )

                ProfileInput(
                    "Full name",
                    studentName,
                    onStudentNameChange
                )

                ProfileInput(
                    "Course",
                    course
                ) { course = it }

                ProfileInput(
                    "University",
                    university
                ) { university = it }
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Career interests & skills",
                    color = Indigo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                ProfileInput(
                    "Career interests",
                    interests
                ) { interests = it }

                ProfileInput(
                    "Skills",
                    skills
                ) { skills = it }
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "CV Analysis",
                    color = Indigo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Upload your CV to extract education, " +
                            "skills, experience and projects.",
                    color = Muted
                )

                Surface(
                    color = Lavender,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.UploadFile,
                            contentDescription = null,
                            tint = Blue,
                            modifier = Modifier.size(36.dp)
                        )

                        Text(
                            text = "PDF only · Maximum 5 MB",
                            color = Indigo
                        )

                        Button(
                            onClick = {
                                picker.launch(
                                    arrayOf("application/pdf")
                                )
                            },
                            enabled = !profileViewModel.isUploading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Blue
                            )
                        ) {
                            Text("Choose CV")
                        }
                    }
                }

                selectionError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (profileViewModel.selectedCvUri != null) {
                    Text(
                        text = profileViewModel.selectedCvName
                            ?: "Selected CV",
                        color = Indigo,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                profileViewModel.uploadCv()
                            },
                            enabled = !profileViewModel.isUploading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Blue
                            )
                        ) {
                            Text("Analyze CV")
                        }

                        OutlinedButton(
                            onClick = profileViewModel::removeCv,
                            enabled = !profileViewModel.isUploading
                        ) {
                            Text("Remove")
                        }
                    }
                }

                if (profileViewModel.isUploading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Blue
                    )

                    Text(
                        text = "Analyzing your CV...",
                        color = Muted
                    )
                }

                profileViewModel.uploadMessage?.let {
                    Text(
                        text = it,
                        color = if (
                            profileViewModel.uploadSucceeded
                        ) Blue else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        profileViewModel.candidateProfile?.let { profile ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Extracted Candidate Profile",
                        color = Indigo,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "These details were extracted " +
                                "from your CV by the backend.",
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )

                    listOf(
                        "Education" to "education",
                        "Experience" to "experience",
                        "Skills" to "skills",
                        "Projects" to "projects",
                        "Career interests" to "career_interests"
                    ).forEach { (label, key) ->
                        ProfileResultSection(
                            title = label,
                            value = profile.get(key)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
private fun ProfileResultSection(
    title: String,
    value: JsonElement?
) {
    if (value == null || value.isJsonNull) return

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Indigo
        )

        val displayText = when {
            value.isJsonArray -> {
                val array = value.asJsonArray

                if (array.size() == 0) {
                    "No information extracted"
                } else {
                    array.joinToString("\n\n") { element ->
                        formatProfileElement(element)
                    }
                }
            }

            else -> formatProfileElement(value)
        }

        Text(
            text = displayText,
            color = Muted,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun formatProfileElement(
    element: JsonElement
): String {
    if (!element.isJsonObject) {
        return element.toString().trim('"')
    }

    val obj: JsonObject = element.asJsonObject

    return obj.entrySet()
        .filter { (key, value) ->
            key != "evidence" &&
                    !value.isJsonNull
        }
        .joinToString("\n") { (key, value) ->
            val readableKey = key
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }

            val readableValue = when {
                value.isJsonPrimitive ->
                    value.asString

                value.isJsonArray ->
                    value.asJsonArray.joinToString(", ") {
                        if (it.isJsonPrimitive) {
                            it.asString
                        } else {
                            it.toString()
                        }
                    }

                else -> value.toString()
            }

            "$readableKey: $readableValue"
        }
}
