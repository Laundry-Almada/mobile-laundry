package com.almalaundry.featured.profile.presentation.viewmodels

import android.content.Context
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

    private fun loadProfileData() {
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
            _state.update { it.copy(isCheckingUpdate = true, updateMessage = null) }
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.github.com/repos/Laundry-Almada/mobile-laundry/releases/latest")
                    .header("Accept", "application/vnd.github.v3+json")
                    .build()
                val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: return@launch)
                    val latestVersionName = json.getString("tag_name").removePrefix("v")
                    val apkUrl = json.getJSONArray("assets")
                        .getJSONObject(0)
                        .getString("browser_download_url")
                    val releaseNotes = json.getString("body").ifBlank { "Tidak ada catatan rilis" }

                    // Ambil versi aplikasi saat ini
                    val currentVersionName = getCurrentVersionName(context)

                    // Bandingkan versi
                    if (isNewerVersion(latestVersionName, currentVersionName)) {
                        _state.update {
                            it.copy(
                                isCheckingUpdate = false,
                                isUpdateAvailable = true,
                                updateMessage = "Versi baru $latestVersionName tersedia! Catatan: $releaseNotes",
                                updateApkUrl = apkUrl
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isCheckingUpdate = false,
                                isUpdateAvailable = false,
                                updateMessage = "Aplikasi sudah menggunakan versi terbaru ($currentVersionName).",
                                updateApkUrl = null
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isCheckingUpdate = false,
                            updateMessage = "Gagal memeriksa pembaruan: ${response.message}",
                            updateApkUrl = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isCheckingUpdate = false,
                        updateMessage = "Error: ${e.message}",
                        updateApkUrl = null
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


//@HiltViewModel
//class ProfileViewModel @Inject constructor(
//    private val authRepository: AuthRepository
//) : ViewModel() {
//    private val _state = MutableStateFlow(ProfileState())
//    val state = _state.asStateFlow()
//
//    init {
//        loadProfileData()
//    }
//
//    private fun loadProfileData() {
//        viewModelScope.launch {
//            // Load data implementation
//        }
//    }
//
//    fun logout() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            authRepository.logout().fold(onSuccess = {
//                _state.update { it.copy(isLoading = false, isLoggedOut = true) }
//            }, onFailure = { error ->
//                _state.update { it.copy(isLoading = false, error = error.message) }
//            })
//        }
//    }
//}
//
