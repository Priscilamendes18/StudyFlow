package br.gov.sp.cps.studyflow.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.gov.sp.cps.studyflow.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "studyflow_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val STUDENT_NAME = stringPreferencesKey("student_name")
        val DAILY_GOAL_HOURS = intPreferencesKey("daily_goal_hours")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            studentName = preferences[STUDENT_NAME] ?: "Estudante",
            dailyGoalHours = preferences[DAILY_GOAL_HOURS] ?: 4,
            isDarkMode = preferences[DARK_MODE] ?: false,
            notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true
        )
    }

    suspend fun updateStudentName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[STUDENT_NAME] = name
        }
    }

    suspend fun updateDailyGoalHours(hours: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_GOAL_HOURS] = hours
        }
    }

    suspend fun updateDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
