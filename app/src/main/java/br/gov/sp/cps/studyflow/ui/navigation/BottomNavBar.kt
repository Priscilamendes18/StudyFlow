package br.gov.sp.cps.studyflow.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun StudyFlowBottomBar(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit
) {
    val items = listOf(
        Screen.Dashboard,
        Screen.Subjects,
        Screen.Tasks,
        Screen.Exams,
        Screen.Settings
    )

    NavigationBar {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToRoute(screen.route) },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) }
            )
        }
    }
}
