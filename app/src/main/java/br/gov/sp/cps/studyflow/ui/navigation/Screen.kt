package br.gov.sp.cps.studyflow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Início", Icons.Default.Home)
    object Subjects : Screen("subjects", "Matérias", Icons.Default.Book)
    object Tasks : Screen("tasks", "Tarefas", Icons.Default.CheckCircle)
    object Exams : Screen("exams", "Provas", Icons.Default.Event)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
}
