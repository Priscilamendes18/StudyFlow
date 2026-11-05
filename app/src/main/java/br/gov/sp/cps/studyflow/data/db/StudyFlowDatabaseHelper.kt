package br.gov.sp.cps.studyflow.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudyFlowDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "studyflow.db"
        const val DATABASE_VERSION = 1

        // Tabela Matérias
        const val TABLE_SUBJECTS = "materias"
        const val COLUMN_SUBJECT_ID = "id"
        const val COLUMN_SUBJECT_NAME = "nome"
        const val COLUMN_SUBJECT_CODE = "codigo"
        const val COLUMN_SUBJECT_PROFESSOR = "professor"
        const val COLUMN_SUBJECT_COLOR = "cor"

        // Tabela Tarefas
        const val TABLE_TASKS = "tarefas"
        const val COLUMN_TASK_ID = "id"
        const val COLUMN_TASK_SUBJECT_ID = "materia_id"
        const val COLUMN_TASK_TITLE = "titulo"
        const val COLUMN_TASK_DESCRIPTION = "descricao"
        const val COLUMN_TASK_DUE_DATE = "data_entrega"
        const val COLUMN_TASK_COMPLETED = "concluida"
        const val COLUMN_TASK_PRIORITY = "prioridade"

        // Tabela Provas / Avaliações
        const val TABLE_EXAMS = "provas"
        const val COLUMN_EXAM_ID = "id"
        const val COLUMN_EXAM_SUBJECT_ID = "materia_id"
        const val COLUMN_EXAM_TITLE = "titulo"
        const val COLUMN_EXAM_DATE = "data_prova"
        const val COLUMN_EXAM_LOCATION = "local"
        const val COLUMN_EXAM_SCORE = "nota"
        const val COLUMN_EXAM_MAX_SCORE = "nota_maxima"
        const val COLUMN_EXAM_NOTES = "observacoes"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createSubjectsTable = """
            CREATE TABLE $TABLE_SUBJECTS (
                $COLUMN_SUBJECT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUBJECT_NAME TEXT NOT NULL,
                $COLUMN_SUBJECT_CODE TEXT,
                $COLUMN_SUBJECT_PROFESSOR TEXT,
                $COLUMN_SUBJECT_COLOR TEXT
            );
        """.trimIndent()

        val createTasksTable = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK_SUBJECT_ID INTEGER NOT NULL,
                $COLUMN_TASK_TITLE TEXT NOT NULL,
                $COLUMN_TASK_DESCRIPTION TEXT,
                $COLUMN_TASK_DUE_DATE INTEGER NOT NULL,
                $COLUMN_TASK_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $COLUMN_TASK_PRIORITY TEXT DEFAULT 'MEDIA',
                FOREIGN KEY($COLUMN_TASK_SUBJECT_ID) REFERENCES $TABLE_SUBJECTS($COLUMN_SUBJECT_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        val createExamsTable = """
            CREATE TABLE $TABLE_EXAMS (
                $COLUMN_EXAM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EXAM_SUBJECT_ID INTEGER NOT NULL,
                $COLUMN_EXAM_TITLE TEXT NOT NULL,
                $COLUMN_EXAM_DATE INTEGER NOT NULL,
                $COLUMN_EXAM_LOCATION TEXT,
                $COLUMN_EXAM_SCORE REAL DEFAULT 0.0,
                $COLUMN_EXAM_MAX_SCORE REAL DEFAULT 10.0,
                $COLUMN_EXAM_NOTES TEXT,
                FOREIGN KEY($COLUMN_EXAM_SUBJECT_ID) REFERENCES $TABLE_SUBJECTS($COLUMN_SUBJECT_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        db.execSQL(createSubjectsTable)
        db.execSQL(createTasksTable)
        db.execSQL(createExamsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXAMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBJECTS")
        onCreate(db)
    }
}
