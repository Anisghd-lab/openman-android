package com.ghdinteractivestudio.openman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ghdinteractivestudio.openman.ui.screens.HomeScreen
import com.ghdinteractivestudio.openman.ui.screens.SettingsScreen
import com.ghdinteractivestudio.openman.ui.screens.SkillsScreen
import com.ghdinteractivestudio.openman.ui.screens.TaskDetailScreen
import com.ghdinteractivestudio.openman.ui.theme.ObsidianBase
import com.ghdinteractivestudio.openman.ui.theme.OpenManTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as OpenManApp
        val taskRepo = app.taskRepository
        val settingsRepo = app.settingsRepository
        val engine = app.agentEngine

        setContent {
            OpenManTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBase
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                taskRepository = taskRepo,
                                settingsRepository = settingsRepo,
                                agentEngine = engine,
                                onNavigateToTask = { taskId ->
                                    navController.navigate("task/$taskId")
                                },
                                onOpenSettings = {
                                    navController.navigate("settings")
                                },
                                onOpenSkills = {
                                    navController.navigate("skills")
                                }
                            )
                        }

                        composable(
                            route = "task/{taskId}",
                            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                            TaskDetailScreen(
                                taskId = taskId,
                                taskRepository = taskRepo,
                                agentEngine = engine,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("skills") {
                            SkillsScreen(
                                taskRepository = taskRepo,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                settingsRepository = settingsRepo,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
