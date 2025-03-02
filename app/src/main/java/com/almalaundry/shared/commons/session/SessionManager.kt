package com.almalaundry.shared.commons.session

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.almalaundry.shared.domain.models.Session
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val SESSION_KEY = stringPreferencesKey("session")
        private const val TAG = "SessionManager"
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun saveSession(session: Session) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_KEY] = json.encodeToString(Session.serializer(), session)
            Log.d(TAG, "Session saved: $session")
        }
    }

    suspend fun getSession(): Session? {
        val session = context.dataStore.data.map { preferences ->
            preferences[SESSION_KEY]?.let { json.decodeFromString(Session.serializer(), it) }
        }.firstOrNull()
        Log.d(TAG, "Session retrieved: $session")
        return session
    }

    val sessionFlow: Flow<Session?> = context.dataStore.data.map { preferences ->
        preferences[SESSION_KEY]?.let { json.decodeFromString(Session.serializer(), it) }
    }

    suspend fun getToken(): String? = getSession()?.token

    suspend fun getLaundryId(): String? = getSession()?.laundryId

    suspend fun getRole(): String? = getSession()?.role

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(SESSION_KEY)
            Log.d(TAG, "Session cleared")
        }
    }

    suspend fun isLoggedIn(): Boolean = getToken() != null
}