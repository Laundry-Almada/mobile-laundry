package com.almalaundry.featured.profile.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.auth.data.repositories.AuthRepository
import com.almalaundry.featured.profile.data.model.UpdateProfileRequest
import com.almalaundry.featured.profile.data.repository.ProfileRepository
import com.almalaundry.featured.profile.presentation.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            profileRepository.getUser().fold(
                onSuccess = { userResponse ->
                    val user = userResponse.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            name = user.name,
                            email = user.email,
                            role = user.role,
                            laundryName = user.laundry.name
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val request = UpdateProfileRequest(
                name = name,
                email = email,
                password = password.ifBlank { null }
            )
            profileRepository.updateProfile(request).fold(
                onSuccess = { userResponse ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            name = userResponse.data.name,
                            email = userResponse.data.email,
                            role = userResponse.data.role
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    onError(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.logout().fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false, isLoggedOut = true) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    // Fungsi untuk cek pembaruan
    fun checkForUpdate(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isCheckingUpdate = true,
                    updateMessage = null,
                    updateApkUrl = null
                )
            }
            try {
                Log.d("ProfileViewModel", "Memulai pengecekan pembaruan")
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.github.com/repos/Laundry-Almada/mobile-laundry/releases/latest")
                    .header("Accept", "application/vnd.github.v3+json")
                    .build()
                val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
                Log.d("ProfileViewModel", "Respons HTTP: ${response.code} ${response.message}")
                if (response.isSuccessful) {
                    val jsonString = response.body?.string() ?: run {
                        _state.update {
                            it.copy(
                                isCheckingUpdate = false,
                                updateMessage = "Gagal memeriksa pembaruan: Respons kosong"
                            )
                        }
                        return@launch
                    }
                    Log.d("ProfileViewModel", "JSON: $jsonString")
                    val json = JSONObject(jsonString)
                    val latestVersionName = json.optString("tag_name", "").removePrefix("v")
                    // Validasi tag_name
                    if (!latestVersionName.matches(Regex("\\d+\\.\\d+\\.\\d+"))) {
                        Log.d("ProfileViewModel", "Format versi tidak valid: $latestVersionName")
                        _state.update {
                            it.copy(
                                isCheckingUpdate = false,
                                updateMessage = "Format versi tidak valid: $latestVersionName"
                            )
                        }
                        return@launch
                    }
                    // Periksa apakah ada assets
                    val assets = json.optJSONArray("assets") ?: run {
                        Log.d("ProfileViewModel", "Tidak ada assets di release")
                        _state.update {
                            it.copy(
                                isCheckingUpdate = false,
                                updateMessage = "Tidak ada file APK di release terbaru"
                            )
                        }
                        return@launch
                    }
                    if (assets.length() == 0) {
                        Log.d("ProfileViewModel", "Assets kosong")
                        _state.update {
                            it.copy(
                                isCheckingUpdate = false,
                                updateMessage = "Tidak ada file APK di release terbaru"
                            )
                        }
                        return@launch
                    }
                    val apkUrl = assets.getJSONObject(0).optString("browser_download_url", "")
                    if (apkUrl.isEmpty()) {
                        Log.d("ProfileViewModel", "URL APK tidak ditemukan")
                        _state.update {
                            it.copy(
                                isCheckingUpdate = false,
                                updateMessage = "URL APK tidak valid"
                            )
                        }
                        return@launch
                    }
                    val releaseNotes =
                        json.optString("body", "").ifBlank { "Tidak ada catatan rilis" }
                    Log.d("ProfileViewModel", "Versi terbaru: $latestVersionName, URL: $apkUrl")

                    // Ambil versi aplikasi saat ini
                    val currentVersionName = getCurrentVersionName(context)
                    Log.d("ProfileViewModel", "Versi saat ini: $currentVersionName")

                    // Bandingkan versi
                    if (isNewerVersion(latestVersionName, currentVersionName)) {
                        Log.d("ProfileViewModel", "Pembaruan tersedia")
                        _state.update {
                            it.copy(
                                isCheckingUpdate = false,
                                isUpdateAvailable = true,
                                updateMessage = "Versi baru $latestVersionName tersedia! Catatan: $releaseNotes",
                                updateApkUrl = apkUrl
                            )
                        }
                    } else {
                        Log.d("ProfileViewModel", "Sudah versi terbaru")
                        _state.update {
                            it.copy(
                                isCheckingUpdate = false,
                                isUpdateAvailable = false,
                                updateMessage = "Aplikasi sudah menggunakan versi terbaru ($currentVersionName)."
                            )
                        }
                    }
                } else {
                    Log.d("ProfileViewModel", "Respons gagal: ${response.code} ${response.message}")
                    _state.update {
                        it.copy(
                            isCheckingUpdate = false,
                            updateMessage = "Gagal memeriksa pembaruan: ${response.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saat cek pembaruan", e)
                _state.update {
                    it.copy(
                        isCheckingUpdate = false,
                        updateMessage = "Error: ${e.message ?: "Tidak dapat memeriksa pembaruan"}"
                    )
                }
            }
        }
    }

    // Fungsi untuk mendapatkan versi aplikasi saat ini
    private fun getCurrentVersionName(context: Context): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.0.0"
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error mendapatkan versi", e)
            "0.0.0"
        }
    }

    // Fungsi untuk membandingkan versi
    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
            val latestPart = latestParts.getOrElse(i) { 0 }
            val currentPart = currentParts.getOrElse(i) { 0 }
            if (latestPart > currentPart) return true
            if (latestPart < currentPart) return false
        }
        return false
    }
}