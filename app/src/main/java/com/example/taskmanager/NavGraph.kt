package com.example.taskmanager

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.taskmanager.data.Task
import com.example.taskmanager.ui.screens.*
import com.example.taskmanager.ui.screens.auth.LoginScreen
import com.example.taskmanager.ui.screens.auth.RegisterScreen
import com.example.taskmanager.viewmodel.TaskViewModel
import com.example.taskmanager.viewmodel.TaskViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    startDestination: String = "login",
    auth: FirebaseAuth
) {
    val navController = rememberNavController()

    // ViewModel setup
    val application = LocalContext.current.applicationContext as Application
    val factory = TaskViewModelFactory(application)
    val taskViewModel: TaskViewModel = viewModel(factory = factory)

    var selectedTask by remember { mutableStateOf<Task?>(null) }

    NavHost(navController = navController, startDestination = startDestination) {

        // Login Screen
        composable("login") {
            LoginScreen(
                navController = navController,
                auth = auth
            )
        }

        // Register Screen
        composable("register") {
            RegisterScreen(
                navController = navController,
                auth = auth
            )
        }

        // Main Task List Screen
        composable("task_list") {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            LaunchedEffect(currentUserId) {
                currentUserId?.let {
                    taskViewModel.setUserId(it) // Set user ID for tasks
                }
            }

            TaskListScreen(
                tasks = taskViewModel.allTasks.collectAsState(initial = emptyList()).value,
                onAddClick = {
                    selectedTask = null
                    navController.navigate("add_edit_task")
                },
                onEditClick = { task ->
                    selectedTask = task
                    navController.navigate("add_edit_task")
                },
                onDeleteClick = { task ->
                    taskViewModel.deleteTask(task)
                },
                taskViewModel = taskViewModel // ✅ Important!
            )
        }

        // Add/Edit Task Screen
        composable("add_edit_task") {
            AddEditTaskScreen(
                taskViewModel = taskViewModel,
                existingTask = selectedTask,
                onSave = {
                    selectedTask = null
                    navController.popBackStack()
                },
                onCancel = {
                    selectedTask = null
                    navController.popBackStack()
                }
            )
        }
    }
}
