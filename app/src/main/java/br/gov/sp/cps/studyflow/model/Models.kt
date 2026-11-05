package br.gov.sp.cps.studyflow.model

data class Subject(
    val id: Long = 0,
    val name: String,
    val code: String = "",
    val professor: String = "",
    val colorHex: String = "#3F51B5"
)

enum class Priority {
    BAIXA, MEDIA, ALTA
}

data class TaskItem(
    val id: Long = 0,
    val subjectId: Long,
    val subjectName: String = "",
    val title: String,
    val description: String = "",
    val dueDateMillis: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIA
)

data class ExamItem(
    val id: Long = 0,
    val subjectId: Long,
    val subjectName: String = "",
    val title: String,
    val examDateMillis: Long = System.currentTimeMillis(),
    val location: String = "",
    val score: Double = 0.0,
    val maxScore: Double = 10.0,
    val notes: String = ""
)

data class BookModel(
    val id: String,
    val title: String,
    val authors: String,
    val description: String,
    val thumbnailUrl: String,
    val infoLink: String
)

data class UserPreferences(
    val studentName: String = "Estudante",
    val dailyGoalHours: Int = 4,
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true
)
