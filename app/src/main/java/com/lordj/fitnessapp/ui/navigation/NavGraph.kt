package com.lordj.fitnessapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.lordj.fitnessapp.ui.screens.exercises.ExerciseDetailScreen
import com.lordj.fitnessapp.ui.screens.exercises.ExerciseListScreen
import com.lordj.fitnessapp.ui.screens.export.ExportScreen
import com.lordj.fitnessapp.ui.screens.garmin.GarminSyncScreen
import com.lordj.fitnessapp.ui.screens.history.HistoryScreen
import com.lordj.fitnessapp.ui.screens.home.HomeScreen
import com.lordj.fitnessapp.ui.screens.progress.ProgressScreen
import com.lordj.fitnessapp.ui.screens.workouts.ActiveWorkoutScreen
import com.lordj.fitnessapp.ui.screens.workouts.WorkoutDetailScreen
import com.lordj.fitnessapp.ui.screens.workouts.WorkoutListScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Filled.Home)
    object Exercises : Screen("exercises", "Ejercicios", Icons.Filled.FitnessCenter)
    object Workouts : Screen("workouts", "Rutinas", Icons.Filled.ListAlt)
    object Progress : Screen("progress", "Progreso", Icons.Filled.BarChart)
    object History : Screen("history", "Historial", Icons.Filled.History)

    object ExerciseDetail : Screen("exercise/{exerciseId}", "", Icons.Filled.Info) {
        fun createRoute(id: Long) = "exercise/$id"
    }
    object WorkoutDetail : Screen("workout/{workoutId}", "", Icons.Filled.Info) {
        fun createRoute(id: Long) = "workout/$id"
    }
    object ActiveWorkout : Screen("active_workout/{workoutId}", "", Icons.Filled.PlayArrow) {
        fun createRoute(id: Long) = "active_workout/$id"
    }
    object Export : Screen("export", "Exportar", Icons.Filled.FileDownload)
    object Garmin : Screen("garmin", "Garmin", Icons.Filled.Watch)
}

val bottomNavItems = listOf(Screen.Home, Screen.Exercises, Screen.Workouts, Screen.Progress, Screen.History)

@Composable
fun FitnessNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { it.route == currentDest?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDest?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) {
                HomeScreen(
                    padding = padding,
                    onNavigateToWorkouts = { navController.navigate(Screen.Workouts.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onStartWorkout = { id -> navController.navigate(Screen.ActiveWorkout.createRoute(id)) },
                    onNavigateToGarmin = { navController.navigate(Screen.Garmin.route) }
                )
            }
            composable(Screen.Exercises.route) {
                ExerciseListScreen(
                    padding = padding,
                    onExerciseClick = { id -> navController.navigate(Screen.ExerciseDetail.createRoute(id)) }
                )
            }
            composable(
                Screen.ExerciseDetail.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
            ) { backStack ->
                val exerciseId = backStack.arguments?.getLong("exerciseId") ?: return@composable
                ExerciseDetailScreen(
                    exerciseId = exerciseId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Workouts.route) {
                WorkoutListScreen(
                    padding = padding,
                    onWorkoutClick = { id -> navController.navigate(Screen.WorkoutDetail.createRoute(id)) }
                )
            }
            composable(
                Screen.WorkoutDetail.route,
                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
            ) { backStack ->
                val workoutId = backStack.arguments?.getLong("workoutId") ?: return@composable
                WorkoutDetailScreen(
                    workoutId = workoutId,
                    onBack = { navController.popBackStack() },
                    onStartWorkout = { id -> navController.navigate(Screen.ActiveWorkout.createRoute(id)) }
                )
            }
            composable(
                Screen.ActiveWorkout.route,
                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
            ) { backStack ->
                val workoutId = backStack.arguments?.getLong("workoutId") ?: return@composable
                ActiveWorkoutScreen(
                    workoutId = workoutId,
                    onFinish = {
                        navController.navigate(Screen.History.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Progress.route) {
                ProgressScreen(padding = padding)
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    padding = padding,
                    onExport = { navController.navigate(Screen.Export.route) }
                )
            }
            composable(Screen.Export.route) {
                ExportScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Garmin.route) {
                GarminSyncScreen(
                    padding = padding,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
