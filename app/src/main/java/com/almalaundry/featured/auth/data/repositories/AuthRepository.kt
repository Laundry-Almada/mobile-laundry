package com.almalaundry.featured.auth.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.almalaundry.featured.auth.data.dtos.AuthData
import com.almalaundry.featured.auth.data.dtos.LoginRequest
import com.almalaundry.featured.auth.data.dtos.RegisterRequest
import com.almalaundry.featured.auth.data.source.AuthApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi, @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    suspend fun login(request: LoginRequest): Result<AuthData> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()!!.data
                saveToken(authData.token)
                Result.success(authData)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthData> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()!!.data
                saveToken(authData.token)
                Result.success(authData)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = api.logout()
            if (response.isSuccessful) {
                clearToken()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.firstOrNull()
    }

    private suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}