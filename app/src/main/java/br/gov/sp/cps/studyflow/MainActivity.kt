package br.gov.sp.cps.studyflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.gov.sp.cps.studyflow.ui.components.AddExamDialog
import br.gov.sp.cps.studyflow.ui.components.AddSubjectDialog
import br.gov.sp.cps.studyflow.ui.components.AddTaskDialog
import br.gov.sp.cps.studyflow.ui.navigation.Screen
import br.gov.sp.cps.studyflow.ui.navigation.StudyFlowBottomBar
import br.gov.sp.cps.studyflow.ui.screens.DashboardScreen
import br.gov.sp.cps.studyflow.ui.screens.ExamsScreen
import br.gov.sp.cps.studyflow.ui.screens.SettingsScreen
import br.gov.sp.cps.studyflow.ui.screens.SubjectsScreen
import br.gov.sp.cps.studyflow.ui.screens.TasksScreen
import br.gov.sp.cps.studyflow.ui.theme.StudyflowTheme
import br.gov.sp.cps.studyflow.viewmodel.StudyFlowViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StudyFlowViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
        // Permissão de notificação processada
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkNotificationPermission()

        setContent {
            val userPreferences by viewModel.userPreferences.collectAsState()
            val subjects by viewModel.subjects.collectAsState()
            val tasks by viewModel.tasks.collectAsState()
            val exams by viewModel.exams.collectAsState()
            val searchedBooks by viewModel.searchedBooks.collectAsState()
            val isSearchingBooks by viewModel.isSearchingBooks.collectAsState()
            val bookSearchError by viewModel.bookSearchError.collectAsState()

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

            var showAddSubjectDialog by remember { mutableStateOf(false) }
            var showAddTaskDialog by remember { mutableStateOf(false) }
            var showAddExamDialog by remember { mutableStateOf(false) }

            val context = LocalContext.current

            StudyflowTheme(darkTheme = userPreferences.isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "StudyFlow",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    },
                    bottomBar = {
                        StudyFlowBottomBar(
                            currentRoute = currentRoute,
                            onNavigateToRoute = { route ->
                                navController.navigate(route) {
                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                userPreferences = userPreferences,
                                subjects = subjects,
                                tasks = tasks,
                                exams = exams,
                                onAddSubjectClick = { showAddSubjectDialog = true },
                                onAddTaskClick = { showAddTaskDialog = true },
                                onAddExamClick = { showAddExamDialog = true },
                                onTaskToggle = { taskId, isCompleted ->
                                    viewModel.toggleTaskCompleted(taskId, isCompleted)
                                }
                            )
                        }

                        composable(Screen.Subjects.route) {
                            SubjectsScreen(
                                subjects = subjects,
                                searchedBooks = searchedBooks,
                                isSearchingBooks = isSearchingBooks,
                                bookSearchError = bookSearchError,
                                onAddSubjectClick = { showAddSubjectDialog = true },
                                onDeleteSubject = { subjectId -> viewModel.deleteSubject(subjectId) },
                                onSearchBooks = { query -> viewModel.searchBooks(query) }
                            )
                        }

                        composable(Screen.Tasks.route) {
                            TasksScreen(
                                tasks = tasks,
                                subjects = subjects,
                                onAddTaskClick = { showAddTaskDialog = true },
                                onTaskToggle = { taskId, isCompleted ->
                                    viewModel.toggleTaskCompleted(taskId, isCompleted)
                                },
                                onDeleteTask = { taskId -> viewModel.deleteTask(taskId) }
                            )
                        }

                        composable(Screen.Exams.route) {
                            ExamsScreen(
                                exams = exams,
                                subjects = subjects,
                                onAddExamClick = { showAddExamDialog = true },
                                onDeleteExam = { examId -> viewModel.deleteExam(examId) }
                            )
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                userPreferences = userPreferences,
                                onUpdateStudentName = { name -> viewModel.updateStudentName(name) },
                                onUpdateDailyGoalHours = { hours -> viewModel.updateDailyGoalHours(hours) },
                                onUpdateDarkMode = { enabled -> viewModel.updateDarkMode(enabled) },
                                onUpdateNotificationsEnabled = { enabled -> viewModel.updateNotificationsEnabled(enabled) },
                                onTestNotificationClick = { viewModel.triggerTestNotification(context) }
                            )
                        }
                    }

                    // Dialogs
                    if (showAddSubjectDialog) {
                        AddSubjectDialog(
                            onDismiss = { showAddSubjectDialog = false },
                            onSave = { name, code, professor, colorHex ->
                                viewModel.addSubject(name, code, professor, colorHex)
                                showAddSubjectDialog = false
                            }
                        )
                    }

                    if (showAddTaskDialog) {
                        AddTaskDialog(
                            subjects = subjects,
                            onDismiss = { showAddTaskDialog = false },
                            onSave = { subjectId, title, description, dueDateMillis, priority, scheduleNotification ->
                                viewModel.addTask(
                                    subjectId = subjectId,
                                    title = title,
                                    description = description,
                                    dueDateMillis = dueDateMillis,
                                    priority = priority,
                                    context = context,
                                    scheduleReminder = scheduleNotification
                                )
                                showAddTaskDialog = false
                            }
                        )
                    }

                    if (showAddExamDialog) {
                        AddExamDialog(
                            subjects = subjects,
                            onDismiss = { showAddExamDialog = false },
                            onSave = { subjectId, title, location, notes, examDateMillis, scheduleNotification ->
                                viewModel.addExam(
                                    subjectId = subjectId,
                                    title = title,
                                    examDateMillis = examDateMillis,
                                    location = location,
                                    notes = notes,
                                    context = context,
                                    scheduleReminder = scheduleNotification
                                )
                                showAddExamDialog = false
                            }
                        )
                    }
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
