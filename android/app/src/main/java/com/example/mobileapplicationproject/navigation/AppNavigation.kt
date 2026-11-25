package com.example.mobileapplicationproject.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.*

import com.example.mobileapplicationproject.ui.screens.HomeScreen
import com.example.mobileapplicationproject.ui.theme.screens.JobDetailScreen
import com.example.mobileapplicationproject.ui.theme.screens.OpportunityListScreen
import com.example.mobileapplicationproject.ui.theme.screens.ProfileScreen
import com.example.mobileapplicationproject.viewmodel.HomeViewModel
import com.example.mobileapplicationproject.viewmodel.ProfileViewModel

private val Blue = Color(0xFF4F6BED)
private val Lavender = Color(0xFFEDE9FE)
private val Background = Color(0xFFF8F9FF)
private val Muted = Color(0xFF69748E)

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val homeViewModel: HomeViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    var studentName by rememberSaveable {
        mutableStateOf("Alex")
    }

    // Share AI recommendations with the Home screen.
    val recommendations = profileViewModel.recommendations

    LaunchedEffect(recommendations) {
        homeViewModel.updateRecommendations(recommendations)
    }

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    fun navigateToJob(id: String) {
        navController.navigate(
            "job/${Uri.encode(id)}"
        )
    }

    val bottomItems = listOf(
        "home" to "Home",
        "saved" to "Saved",
        "applied" to "Applied",
        "profile" to "Profile"
    )

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (currentRoute != "job/{jobId}") {
                NavigationBar(
                    containerColor = Background
                ) {
                    bottomItems.forEachIndexed { index, item ->

                        val icon = when (index) {
                            0 -> Icons.Filled.Home
                            1 -> Icons.Outlined.BookmarkBorder
                            2 -> Icons.Outlined.CheckCircle
                            else -> Icons.Filled.Person
                        }

                        NavigationBarItem(
                            selected = currentRoute == item.first,
                            onClick = {
                                navController.navigate(item.first) {
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = item.second
                                )
                            },
                            label = {
                                Text(item.second)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue,
                                selectedTextColor = Blue,
                                indicatorColor = Lavender,
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {

            composable("home") {
                HomeScreen(
                    viewModel = homeViewModel,
                    studentName = studentName,
                    onProfileClick = {
                        navController.navigate("profile")
                    },
                    onJobClick = { id ->
                        navigateToJob(id)
                    },
                    onSavedClick = {
                        navController.navigate("saved")
                    },
                    onAppliedClick = {
                        navController.navigate("applied")
                    }
                )
            }

            composable("saved") {
                OpportunityListScreen(
                    title = "Saved Opportunities",
                    jobs = homeViewModel.savedJobs,
                    onJobClick = { id ->
                        navigateToJob(id)
                    },
                    onSaveClick = homeViewModel::toggleSaved
                )
            }

            composable("applied") {
                OpportunityListScreen(
                    title = "My Applications",
                    jobs = homeViewModel.appliedJobs,
                    onJobClick = { id ->
                        navigateToJob(id)
                    },
                    onSaveClick = homeViewModel::toggleSaved
                )
            }

            composable("profile") {
                ProfileScreen(
                    studentName = studentName,
                    onStudentNameChange = {
                        studentName = it
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    profileViewModel = profileViewModel
                )
            }

            composable(
                route = "job/{jobId}",
                arguments = listOf(
                    navArgument("jobId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                val id = entry.arguments
                    ?.getString("jobId")

                val job = homeViewModel.jobs.find {
                    it.id == id
                }

                val recommendation = id?.let {
                    homeViewModel.getRecommendation(it)
                }

                JobDetailScreen(
                    job = job,
                    recommendation = recommendation,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
