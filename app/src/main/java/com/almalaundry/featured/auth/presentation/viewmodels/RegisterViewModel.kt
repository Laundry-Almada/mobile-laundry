package com.almalaundry.featured.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.auth.data.dtos.LaundryRequest
import com.almalaundry.featured.auth.data.dtos.RegisterRequest
import com.almalaundry.featured.auth.data.repositories.AuthRepository
import com.almalaundry.featured.auth.presentation.state.RegisterState
import com.almalaundry.featured.order.domain.models.Laundry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _laundries = MutableStateFlow<List<Laundry>>(emptyList())

    init {
        getLaundries()
    }

    fun onRoleChange(value: String) {
        _state.update {
            it.copy(
                role = value,
                selectedLaundry = "",
                laundryName = "",
                laundryAddress = "",
                laundryPhone = ""
            )
        }
        getLaundries()
    }

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordError = null,
                confirmPasswordError = if (state.value.confirmPassword.isNotEmpty() && state.value.confirmPassword != password)
                    "Konfirmasi password tidak cocok"
                else null
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        val currentState = _state.value
        _state.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (currentState.password.isNotEmpty() && confirmPassword != currentState.password)
                    "Konfirmasi password tidak cocok"
                else null
            )
        }
    }

    fun onLaundryNameChange(laundryName: String) {
        _state.update { it.copy(laundryName = laundryName) }
    }

    fun onLaundryAddressChange(address: String) {
        _state.update { it.copy(laundryAddress = address) }
    }

    fun onLaundryPhoneChange(phone: String) {
        _state.update { it.copy(laundryPhone = phone) }
    }

    fun onSelectedLaundryChange(selectedLaundry: String) {
        _state.update { it.copy(selectedLaundry = selectedLaundry) }
    }

    fun createLaundry() {
        val currentState = _state.value
        when {
            currentState.laundryName.isBlank() -> {
                _state.update { it.copy(error = "Nama laundry tidak boleh kosong") }
                return
            }

            currentState.laundryAddress.isBlank() -> {
                _state.update { it.copy(error = "Alamat laundry tidak boleh kosong") }
                return
            }

            currentState.laundryPhone.isBlank() -> {
                _state.update { it.copy(error = "Nomor telepon tidak boleh kosong") }
                return
            }

            !currentState.laundryPhone.startsWith("8") -> {
                _state.update { it.copy(error = "Nomor telepon harus diawali dengan 8") }
                return
            }
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val request = LaundryRequest(
                name = currentState.laundryName,
                address = currentState.laundryAddress,
                phone = currentState.laundryPhone
            )
            authRepository.createLaundry(request).fold(
                onSuccess = { laundry ->
                    _laundries.value += laundry
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            availableLaundries = _laundries.value.map { laundryItem -> laundryItem.name },
                            selectedLaundry = laundry.name,
                            laundryName = "",
                            laundryAddress = "",
                            laundryPhone = ""
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Gagal membuat laundry"
                        )
                    }
                }
            )
        }
    }

    fun register() {
        val currentState = _state.value
        when {
            currentState.name.isBlank() -> {
                _state.update { it.copy(error = "Nama tidak boleh kosong") }
                return
            }

            currentState.email.isBlank() -> {
                _state.update { it.copy(error = "Email tidak boleh kosong") }
                return
            }

            currentState.password.isBlank() -> {
                _state.update { it.copy(error = "Password tidak boleh kosong") }
                return
            }

            currentState.password.length < 8 -> {
                _state.update { it.copy(passwordError = "Password minimal 8 karakter") }
                return
            }

            !currentState.password.contains(Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$")) -> {
                _state.update { it.copy(passwordError = "Password harus mengandung huruf dan angka") }
                return
            }

            currentState.confirmPassword != currentState.password -> {
                _state.update { it.copy(confirmPasswordError = "Konfirmasi password tidak cocok") }
                return
            }

            currentState.selectedLaundry.isBlank() -> {
                _state.update { it.copy(error = "Pilih laundry terlebih dahulu") }
                return
            }
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    passwordError = null,
                    confirmPasswordError = null
                )
            }

            val laundryId = _laundries.value
                .firstOrNull { it.name == currentState.selectedLaundry }
                ?.id ?: ""

            if (laundryId.isBlank()) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Laundry tidak valid"
                    )
                }
                return@launch
            }

            val request = RegisterRequest(
                name = currentState.name,
                email = currentState.email,
                password = currentState.password,
                confirmPassword = currentState.confirmPassword,
                role = currentState.role,
                laundryId = laundryId
            )

            authRepository.register(request).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Registrasi gagal"
                        )
                    }
                }
            )
        }
    }

    private fun getLaundries() {
        viewModelScope.launch {
            authRepository.getLaundries().fold(
                onSuccess = { laundries ->
                    _laundries.value = laundries
                    _state.update { currentState ->
                        currentState.copy(
                            availableLaundries = laundries.map { laundry -> laundry.name }
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            error = "Gagal memuat laundry: ${error.message}"
                        )
                    }
                }
            )
        }
    }
}