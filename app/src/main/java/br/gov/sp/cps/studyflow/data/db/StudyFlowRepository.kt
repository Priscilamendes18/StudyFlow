package br.gov.sp.cps.studyflow.data.db

import android.content.ContentValues
import android.content.Context
import br.gov.sp.cps.studyflow.model.ExamItem
import br.gov.sp.cps.studyflow.model.Priority
import br.gov.sp.cps.studyflow.model.Subject
import br.gov.sp.cps.studyflow.model.TaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StudyFlowRepository(context: Context) {
    private val dbHelper = StudyFlowDatabaseHelper(context)

    // ==================== SUBJECTS (MATÉRIAS) ====================

    suspend fun insertSubject(subject: Subject): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudyFlowDatabaseHelper.COLUMN_SUBJECT_NAME, subject.name)
            put(StudyFlowDatabaseHelper.COLUMN_SUBJECT_CODE, subject.code)
            put(StudyFlowDatabaseHelper.COLUMN_SUBJECT_PROFESSOR, subject.professor)
            put(StudyFlowDatabaseHelper.COLUMN_SUBJECT_COLOR, subject.colorHex)
        }
        db.insert(StudyFlowDatabaseHelper.TABLE_SUBJECTS, null, values)
    }

    suspend fun updateSubject(subject: Subject): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudyFlowDatabaseHelper.COLUMN_SUBJECT_NAME, subject.name)
            put(StudyFlowDatabaseHelper.COLUMN_SUBJECT_CODE, subject.code)
            put(StudyFlowDatabaseHelper.COLUMN_SUBJECT_PROFESSOR, subject.professor)
            put(StudyFlowDatabaseHelper.COLUMN_SUBJECT_COLOR, subject.colorHex)
        }
        db.update(
            StudyFlowDatabaseHelper.TABLE_SUBJECTS,
            values,
            "${StudyFlowDatabaseHelper.COLUMN_SUBJECT_ID} = ?",
            arrayOf(subject.id.toString())
        )
    }

    suspend fun deleteSubject(subjectId: Long): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        // Apaga também tarefas e provas dessa matéria
        db.delete(
            StudyFlowDatabaseHelper.TABLE_TASKS,
            "${StudyFlowDatabaseHelper.COLUMN_TASK_SUBJECT_ID} = ?",
            arrayOf(subjectId.toString())
        )
        db.delete(
            StudyFlowDatabaseHelper.TABLE_EXAMS,
            "${StudyFlowDatabaseHelper.COLUMN_EXAM_SUBJECT_ID} = ?",
            arrayOf(subjectId.toString())
        )
        db.delete(
            StudyFlowDatabaseHelper.TABLE_SUBJECTS,
            "${StudyFlowDatabaseHelper.COLUMN_SUBJECT_ID} = ?",
            arrayOf(subjectId.toString())
        )
    }

    suspend fun getAllSubjects(): List<Subject> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Subject>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            StudyFlowDatabaseHelper.TABLE_SUBJECTS,
            null, null, null, null, null,
            "${StudyFlowDatabaseHelper.COLUMN_SUBJECT_NAME} ASC"
        )
        cursor.use {
            if (it.moveToFirst()) {
                val idIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_SUBJECT_ID)
                val nameIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_SUBJECT_NAME)
                val codeIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_SUBJECT_CODE)
                val profIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_SUBJECT_PROFESSOR)
                val colorIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_SUBJECT_COLOR)
                do {
                    list.add(
                        Subject(
                            id = it.getLong(idIdx),
                            name = it.getString(nameIdx),
                            code = it.getString(codeIdx) ?: "",
                            professor = it.getString(profIdx) ?: "",
                            colorHex = it.getString(colorIdx) ?: "#3F51B5"
                        )
                    )
                } while (it.moveToNext())
            }
        }
        list
    }

    // ==================== TASKS (TAREFAS / TRABALHOS) ====================

    suspend fun insertTask(task: TaskItem): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudyFlowDatabaseHelper.COLUMN_TASK_SUBJECT_ID, task.subjectId)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_TITLE, task.title)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_DESCRIPTION, task.description)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_DUE_DATE, task.dueDateMillis)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_COMPLETED, if (task.isCompleted) 1 else 0)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_PRIORITY, task.priority.name)
        }
        db.insert(StudyFlowDatabaseHelper.TABLE_TASKS, null, values)
    }

    suspend fun updateTask(task: TaskItem): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudyFlowDatabaseHelper.COLUMN_TASK_SUBJECT_ID, task.subjectId)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_TITLE, task.title)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_DESCRIPTION, task.description)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_DUE_DATE, task.dueDateMillis)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_COMPLETED, if (task.isCompleted) 1 else 0)
            put(StudyFlowDatabaseHelper.COLUMN_TASK_PRIORITY, task.priority.name)
        }
        db.update(
            StudyFlowDatabaseHelper.TABLE_TASKS,
            values,
            "${StudyFlowDatabaseHelper.COLUMN_TASK_ID} = ?",
            arrayOf(task.id.toString())
        )
    }

    suspend fun toggleTaskCompleted(taskId: Long, isCompleted: Boolean): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudyFlowDatabaseHelper.COLUMN_TASK_COMPLETED, if (isCompleted) 1 else 0)
        }
        db.update(
            StudyFlowDatabaseHelper.TABLE_TASKS,
            values,
            "${StudyFlowDatabaseHelper.COLUMN_TASK_ID} = ?",
            arrayOf(taskId.toString())
        )
    }

    suspend fun deleteTask(taskId: Long): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(
            StudyFlowDatabaseHelper.TABLE_TASKS,
            "${StudyFlowDatabaseHelper.COLUMN_TASK_ID} = ?",
            arrayOf(taskId.toString())
        )
    }

    suspend fun getAllTasks(): List<TaskItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<TaskItem>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT T.*, S.${StudyFlowDatabaseHelper.COLUMN_SUBJECT_NAME} AS subject_name 
            FROM ${StudyFlowDatabaseHelper.TABLE_TASKS} T
            LEFT JOIN ${StudyFlowDatabaseHelper.TABLE_SUBJECTS} S 
            ON T.${StudyFlowDatabaseHelper.COLUMN_TASK_SUBJECT_ID} = S.${StudyFlowDatabaseHelper.COLUMN_SUBJECT_ID}
            ORDER BY T.${StudyFlowDatabaseHelper.COLUMN_TASK_COMPLETED} ASC, T.${StudyFlowDatabaseHelper.COLUMN_TASK_DUE_DATE} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        cursor.use {
            if (it.moveToFirst()) {
                val idIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_TASK_ID)
                val subIdIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_TASK_SUBJECT_ID)
                val subNameIdx = it.getColumnIndexOrThrow("subject_name")
                val titleIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_TASK_TITLE)
                val descIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_TASK_DESCRIPTION)
                val dateIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_TASK_DUE_DATE)
                val compIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_TASK_COMPLETED)
                val prioIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_TASK_PRIORITY)
                do {
                    val priorityStr = it.getString(prioIdx) ?: Priority.MEDIA.name
                    val priority = try { Priority.valueOf(priorityStr) } catch (e: Exception) { Priority.MEDIA }
                    list.add(
                        TaskItem(
                            id = it.getLong(idIdx),
                            subjectId = it.getLong(subIdIdx),
                            subjectName = it.getString(subNameIdx) ?: "Sem Matéria",
                            title = it.getString(titleIdx),
                            description = it.getString(descIdx) ?: "",
                            dueDateMillis = it.getLong(dateIdx),
                            isCompleted = it.getInt(compIdx) == 1,
                            priority = priority
                        )
                    )
                } while (it.moveToNext())
            }
        }
        list
    }

    // ==================== EXAMS (PROVAS / AVALIAÇÕES) ====================

    suspend fun insertExam(exam: ExamItem): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_SUBJECT_ID, exam.subjectId)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_TITLE, exam.title)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_DATE, exam.examDateMillis)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_LOCATION, exam.location)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_SCORE, exam.score)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_MAX_SCORE, exam.maxScore)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_NOTES, exam.notes)
        }
        db.insert(StudyFlowDatabaseHelper.TABLE_EXAMS, null, values)
    }

    suspend fun updateExam(exam: ExamItem): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_SUBJECT_ID, exam.subjectId)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_TITLE, exam.title)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_DATE, exam.examDateMillis)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_LOCATION, exam.location)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_SCORE, exam.score)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_MAX_SCORE, exam.maxScore)
            put(StudyFlowDatabaseHelper.COLUMN_EXAM_NOTES, exam.notes)
        }
        db.update(
            StudyFlowDatabaseHelper.TABLE_EXAMS,
            values,
            "${StudyFlowDatabaseHelper.COLUMN_EXAM_ID} = ?",
            arrayOf(exam.id.toString())
        )
    }

    suspend fun deleteExam(examId: Long): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(
            StudyFlowDatabaseHelper.TABLE_EXAMS,
            "${StudyFlowDatabaseHelper.COLUMN_EXAM_ID} = ?",
            arrayOf(examId.toString())
        )
    }

    suspend fun getAllExams(): List<ExamItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<ExamItem>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT E.*, S.${StudyFlowDatabaseHelper.COLUMN_SUBJECT_NAME} AS subject_name 
            FROM ${StudyFlowDatabaseHelper.TABLE_EXAMS} E
            LEFT JOIN ${StudyFlowDatabaseHelper.TABLE_SUBJECTS} S 
            ON E.${StudyFlowDatabaseHelper.COLUMN_EXAM_SUBJECT_ID} = S.${StudyFlowDatabaseHelper.COLUMN_SUBJECT_ID}
            ORDER BY E.${StudyFlowDatabaseHelper.COLUMN_EXAM_DATE} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        cursor.use {
            if (it.moveToFirst()) {
                val idIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_EXAM_ID)
                val subIdIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_EXAM_SUBJECT_ID)
                val subNameIdx = it.getColumnIndexOrThrow("subject_name")
                val titleIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_EXAM_TITLE)
                val dateIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_EXAM_DATE)
                val locIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_EXAM_LOCATION)
                val scoreIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_EXAM_SCORE)
                val maxIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_EXAM_MAX_SCORE)
                val notesIdx = it.getColumnIndexOrThrow(StudyFlowDatabaseHelper.COLUMN_EXAM_NOTES)
                do {
                    list.add(
                        ExamItem(
                            id = it.getLong(idIdx),
                            subjectId = it.getLong(subIdIdx),
                            subjectName = it.getString(subNameIdx) ?: "Sem Matéria",
                            title = it.getString(titleIdx),
                            examDateMillis = it.getLong(dateIdx),
                            location = it.getString(locIdx) ?: "",
                            score = it.getDouble(scoreIdx),
                            maxScore = it.getDouble(maxIdx),
                            notes = it.getString(notesIdx) ?: ""
                        )
                    )
                } while (it.moveToNext())
            }
        }
        list
    }
}
