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
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return
        viewModelScope.launch {
            _isSearchingBooks.value = true
            _bookSearchError.value = null
            try {
                val response = GoogleBooksClient.api.searchBooks(query = cleanQuery)
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

                if (items.isNotEmpty()) {
                    _searchedBooks.value = items
                    _bookSearchError.value = null
                } else {
                    _searchedBooks.value = getFallbackBooks(cleanQuery)
                    _bookSearchError.value = "Nenhum livro retornado da API. Exibindo livros de estudo recomendados para '$cleanQuery'."
                }
            } catch (e: retrofit2.HttpException) {
                _searchedBooks.value = getFallbackBooks(cleanQuery)
                if (e.code() == 429) {
                    _bookSearchError.value = "A API do Google Books atingiu o limite temporário de acessos. Exibindo sugestões de livros recomendados para '$cleanQuery'."
                } else {
                    _bookSearchError.value = "Erro HTTP ${e.code()} na API Google Books. Exibindo sugestões de livros recomendados para '$cleanQuery'."
                }
            } catch (e: Exception) {
                _searchedBooks.value = getFallbackBooks(cleanQuery)
                _bookSearchError.value = "Falha ao conectar à API Google Books. Exibindo sugestões recomendadas para '$cleanQuery'."
            } finally {
                _isSearchingBooks.value = false
            }
        }
    }

    private fun getFallbackBooks(query: String): List<BookModel> {
        val q = query.lowercase(java.util.Locale.ROOT)
        return when {
            q.contains("matem") || q.contains("calc") || q.contains("álgebra") -> listOf(
                BookModel(
                    id = "fb1",
                    title = "Cálculo - Volume 1",
                    authors = "James Stewart",
                    description = "Livro-texto essencial e referência indispensável para disciplinas de cálculo diferencial e integral.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                ),
                BookModel(
                    id = "fb2",
                    title = "Álgebra Linear e Suas Aplicações",
                    authors = "David C. Lay",
                    description = "Conceitos fundamentais de matrizes, espaços vetoriais e transformações lineares.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                ),
                BookModel(
                    id = "fb3",
                    title = "Fundamentos de Matemática Elementar",
                    authors = "Gelson Iezzi",
                    description = "Volume indispensável para revisão e base matemática acadêmica.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                )
            )
            q.contains("prog") || q.contains("algoritm") || q.contains("comput") || q.contains("código") || q.contains("java") || q.contains("kotlin") -> listOf(
                BookModel(
                    id = "fb4",
                    title = "Algoritmos: Teoria e Prática",
                    authors = "Thomas H. Cormen",
                    description = "A bíblia dos algoritmos e estruturas de dados para estudantes de engenharia e computação.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                ),
                BookModel(
                    id = "fb5",
                    title = "Código Limpo (Clean Code)",
                    authors = "Robert C. Martin",
                    description = "Habilidades práticas para escrever código legível, sustentável e de alta qualidade.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                ),
                BookModel(
                    id = "fb6",
                    title = "Entendendo Algoritmos",
                    authors = "Aditya Y. Bhargava",
                    description = "Um guia ilustrado e prático para estudantes de programação e sistemas de informação.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                )
            )
            q.contains("físic") || q.contains("mecan") || q.contains("eletro") -> listOf(
                BookModel(
                    id = "fb7",
                    title = "Física para Cientistas e Engenheiros - Vol 1",
                    authors = "Paul A. Tipler",
                    description = "Fundamentos de mecânica, oscilações e termodinâmica no ensino superior.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                ),
                BookModel(
                    id = "fb8",
                    title = "Fundamentos de Física - Mecânica",
                    authors = "Halliday & Resnick",
                    description = "Referência acadêmica mundial para cursos superiores de exatas e engenharias.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                )
            )
            else -> listOf(
                BookModel(
                    id = "fb9",
                    title = "Como Estudar e Aprender Melhores Métodos",
                    authors = "Barbara Oakley",
                    description = "Técnicas de aprendizado acelerado, foco e gerenciamento de tempo acadêmico.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                ),
                BookModel(
                    id = "fb10",
                    title = "Metodologia Científica para Iniciantes",
                    authors = "Eva Maria Lakatos",
                    description = "Guia completo para organização de pesquisas, trabalhos acadêmicos e TCC.",
                    thumbnailUrl = "",
                    infoLink = "https://books.google.com"
                )
            )
        }
    }
}
