package br.gov.sp.cps.studyflow.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.gov.sp.cps.studyflow.data.api.GoogleBooksClient
import br.gov.sp.cps.studyflow.data.datastore.UserPreferencesRepository
import br.gov.sp.cps.studyflow.data.db.StudyFlowRepository
import br.gov.sp.cps.studyflow.model.BookModel
import br.gov.sp.cps.studyflow.model.ExamItem
import br.gov.sp.cps.studyflow.model.Priority
import br.gov.sp.cps.studyflow.model.Subject
import br.gov.sp.cps.studyflow.model.TaskItem
import br.gov.sp.cps.studyflow.model.UserPreferences
import br.gov.sp.cps.studyflow.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val dbRepository = StudyFlowRepository(application)
    private val preferencesRepository = UserPreferencesRepository(application)

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _exams = MutableStateFlow<List<ExamItem>>(emptyList())
    val exams: StateFlow<List<ExamItem>> = _exams.asStateFlow()

    private val _userPreferences = MutableStateFlow(UserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private val _searchedBooks = MutableStateFlow<List<BookModel>>(emptyList())
    val searchedBooks: StateFlow<List<BookModel>> = _searchedBooks.asStateFlow()

    private val _isSearchingBooks = MutableStateFlow(false)
    val isSearchingBooks: StateFlow<Boolean> = _isSearchingBooks.asStateFlow()

    private val _bookSearchError = MutableStateFlow<String?>(null)
    val bookSearchError: StateFlow<String?> = _bookSearchError.asStateFlow()

    init {
        loadData()
        observePreferences()
    }

    fun loadData() {
        viewModelScope.launch {
            _subjects.value = dbRepository.getAllSubjects()
            _tasks.value = dbRepository.getAllTasks()
            _exams.value = dbRepository.getAllExams()
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collect { prefs ->
                _userPreferences.value = prefs
            }
        }
    }

    // ==================== MATÉRIAS ====================

    fun addSubject(name: String, code: String, professor: String, colorHex: String) {
        viewModelScope.launch {
            val newSubject = Subject(
                name = name,
                code = code,
                professor = professor,
                colorHex = colorHex
            )
            dbRepository.insertSubject(newSubject)
            loadData()
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            dbRepository.updateSubject(subject)
            loadData()
        }
    }

    fun deleteSubject(subjectId: Long) {
        viewModelScope.launch {
            dbRepository.deleteSubject(subjectId)
            loadData()
        }
    }

    // ==================== TAREFAS ====================

    fun addTask(
        subjectId: Long,
        title: String,
        description: String,
        dueDateMillis: Long,
        priority: Priority,
        context: Context? = null,
        scheduleReminder: Boolean = false
    ) {
        viewModelScope.launch {
            val task = TaskItem(
                subjectId = subjectId,
                title = title,
                description = description,
                dueDateMillis = dueDateMillis,
                isCompleted = false,
                priority = priority
            )
            val newId = dbRepository.insertTask(task)
            loadData()

            if (scheduleReminder && context != null && newId > 0) {
                val subjectName = subjects.value.find { it.id == subjectId }?.name ?: "Matéria"
                NotificationHelper.scheduleNotification(
                    context = context,
                    notificationId = newId.toInt() + 1000,
                    title = "Lembrete de Tarefa: $title",
                    message = "Entrega da tarefa de $subjectName é em breve!",
                    triggerAtMillis = dueDateMillis
                )
            }
        }
    }

    fun toggleTaskCompleted(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            dbRepository.toggleTaskCompleted(taskId, isCompleted)
            loadData()
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            dbRepository.deleteTask(taskId)
            loadData()
        }
    }

    // ==================== PROVAS ====================

    fun addExam(
        subjectId: Long,
        title: String,
        examDateMillis: Long,
        location: String,
        notes: String,
        score: Double = 0.0,
        maxScore: Double = 10.0,
        context: Context? = null,
        scheduleReminder: Boolean = false
    ) {
        viewModelScope.launch {
            val exam = ExamItem(
                subjectId = subjectId,
                title = title,
                examDateMillis = examDateMillis,
                location = location,
                notes = notes,
                score = score,
                maxScore = maxScore
            )
            val newId = dbRepository.insertExam(exam)
            loadData()

            if (scheduleReminder && context != null && newId > 0) {
                val subjectName = subjects.value.find { it.id == subjectId }?.name ?: "Matéria"
                NotificationHelper.scheduleNotification(
                    context = context,
                    notificationId = newId.toInt() + 5000,
                    title = "Lembrete de Prova: $title",
                    message = "Sua avaliação de $subjectName ($title) está agendada!",
                    triggerAtMillis = examDateMillis
                )
            }
        }
    }

    fun updateExam(exam: ExamItem) {
        viewModelScope.launch {
            dbRepository.updateExam(exam)
            loadData()
        }
    }

    fun deleteExam(examId: Long) {
        viewModelScope.launch {
            dbRepository.deleteExam(examId)
            loadData()
        }
    }

    // ==================== PREFERÊNCIAS (DATASTORE) ====================

    fun updateStudentName(name: String) {
        viewModelScope.launch {
            preferencesRepository.updateStudentName(name)
        }
    }

    fun updateDailyGoalHours(hours: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDailyGoalHours(hours)
        }
    }

    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDarkMode(enabled)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateNotificationsEnabled(enabled)
        }
    }

    fun triggerTestNotification(context: Context) {
        NotificationHelper.showImmediateNotification(
            context = context,
            title = "StudyFlow Notificações",
            message = "As notificações do seu aplicativo estão funcionando perfeitamente!"
        )
    }

    // ==================== PESQUISA DE LIVROS (API GOOGLE BOOKS) ====================

    fun searchBooks(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isSearchingBooks.value = true
            _bookSearchError.value = null
            try {
                val response = GoogleBooksClient.api.searchBooks(query = query)
                val items = response.items?.mapNotNull { item ->
                    val info = item.volumeInfo ?: return@mapNotNull null
                    val thumbnail = info.imageLinks?.thumbnail
                        ?: info.imageLinks?.smallThumbnail
                        ?: ""
                    // Converte http para https para evitar bloqueio de cleartext
                    val secureThumbnail = thumbnail.replace("http://", "https://")

                    BookModel(
                        id = item.id ?: System.currentTimeMillis().toString(),
                        title = info.title ?: "Sem Título",
                        authors = info.authors?.joinToString(", ") ?: "Autor Desconhecido",
                        description = info.description ?: "Sem descrição disponível.",
                        thumbnailUrl = secureThumbnail,
                        infoLink = info.infoLink ?: "https://books.google.com"
                    )
                } ?: emptyList()

                _searchedBooks.value = items
                if (items.isEmpty()) {
                    _bookSearchError.value = "Nenhum livro encontrado para '$query'."
                }
            } catch (e: Exception) {
                _bookSearchError.value = "Erro ao buscar livros: ${e.localizedMessage ?: "Verifique sua conexão"}"
                _searchedBooks.value = emptyList()
            } finally {
                _isSearchingBooks.value = false
            }
        }
    }
}
