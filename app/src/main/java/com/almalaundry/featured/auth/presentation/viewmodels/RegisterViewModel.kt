package com.almalaundry.featured.auth.presentation.viewmodels

//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.almalaundry.featured.auth.data.dtos.RegisterDto
//import com.almalaundry.featured.auth.domain.models.AuthRepository
//import com.almalaundry.featured.auth.presentation.state.RegisterState
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import javax.inject.Inject

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.auth.data.dtos.RegisterDto
import com.almalaundry.featured.auth.domain.models.AuthRepository
import com.almalaundry.featured.auth.presentation.state.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
//class RegisterViewModel @Inject constructor(
//    private val authRepository: AuthRepository
//) : ViewModel() {
//    private val _state = MutableStateFlow(RegisterState())
//    val state = _state.asStateFlow()
//
//    fun onUsernameChange(username: String) {
//        _state.update { it.copy(username = username) }
//    }
//
//    fun onPasswordChange(password: String) {
//        _state.update { it.copy(password = password) }
//    }
//
//    fun onEmailChange(email: String) {
//        _state.update { it.copy(email = email) }
//    }
//
//    fun register() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//
//            val result = authRepository.register(
//                RegisterDto(
//                    username = state.value.username,
//                    password = state.value.password,
//                    email = state.value.email
//                )
//            )
//
//            result.fold(
//                onSuccess = { user ->
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            isSuccess = true
//                        )
//                    }
//                },
//                onFailure = { error ->
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            error = error.message
//                        )
//                    }
//                }
//            )
//        }
//    }
//}
//

//@HiltViewModel
//class RegisterViewModel @Inject constructor(
//    private val authRepository: AuthRepository
//) : ViewModel() {
//    private val _state = MutableStateFlow(RegisterState())
//    val state = _state.asStateFlow()
//    var username by mutableStateOf("")
//    private var email by mutableStateOf("")
//    private var password by mutableStateOf("")
//    private var confirmPassword by mutableStateOf("")
//    private var role by mutableStateOf("Owner")
//    private var laundryName by mutableStateOf("")
//    private var availableLaundries by mutableStateOf(emptyList<String>())
//    private var selectedLaundry by mutableStateOf("")
//
//    fun onUsernameChange(username: String) {
//        _state.update { it.copy(username = username) }
//    }
//
//    fun onEmailChange(email: String) {
//        _state.update { it.copy(email = email) }
//    }
//
//    fun onPasswordChange(password: String) {
//        _state.update { it.copy(password = password) }
//    }
//
//    fun onConfirmPasswordChange(confirmPassword: String) {
//        _state.update { it.copy(confirmPassword = confirmPassword) }
//    }
//
//
//    fun onRoleChange(value: String) {
//        _state.value = _state.value.copy(
//            role = value,
//            availableLaundries = if (value == "Staff") listOf("Laundry Almada", "Laundry Balmada") else emptyList(),
//            selectedLaundry = if (value == "Staff") "" else _state.value.selectedLaundry,
//            laundryName = if (value == "Owner") "" else _state.value.laundryName
//        )
//    }
//
//
//    fun onLaundryNameChange(laundryName: String) {
//        _state.update { it.copy(laundryName = laundryName) }
//    }
//
//    fun onSelectedLaundryChange(selectedLaundry: String) {
//        _state.update { it.copy(selectedLaundry = selectedLaundry) }
//    }
//
//    fun register() {
//        if (password != confirmPassword) {
//            _state.update { it.copy(error = "Password dan Confirm Password harus sama!") }
//            return
//        }
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//
//            val result = authRepository.register(
//                RegisterDto(
//                    username = state.value.username,
//                    password = state.value.password,
//                    email = state.value.email
//                )
//            )
//
//            result.fold(
//                onSuccess = {
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            isSuccess = true
//                        )
//                    }
//                },
//                onFailure = { error ->
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            error = error.message
//                        )
//                    }
//                }
//            )
//        }
//    }
//}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onUsernameChange(username: String) {
        _state.update { it.copy(username = username) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onRoleChange(value: String) {
        _state.update {
            it.copy(
                role = value,
                availableLaundries = if (value == "Staff") listOf("Laundry Almada", "Laundry Balmada") else emptyList(),
                selectedLaundry = if (value == "Staff") "" else it.selectedLaundry,
                laundryName = if (value == "Owner") "" else it.laundryName
            )
        }
    }

    fun onLaundryNameChange(laundryName: String) {
        _state.update { it.copy(laundryName = laundryName) }
    }

    fun onSelectedLaundryChange(selectedLaundry: String) {
        _state.update { it.copy(selectedLaundry = selectedLaundry) }
    }

    fun register() {
        val currentState = _state.value
        when {
            currentState.username.isBlank() -> {
                _state.update { it.copy(error = "Username tidak boleh kosong!") }
                return
            }
            currentState.email.isBlank() -> {
                _state.update { it.copy(error = "Email tidak boleh kosong!") }
                return
            }
            currentState.password.isBlank() -> {
                _state.update {it.copy(error = "Password tidak boleh kosong")}
                return
            }
            currentState.password != currentState.confirmPassword -> {
                _state.update { it.copy(error = "Password dan Confirm Password harus sama!") }
                return
            }
            currentState.role == "Owner" && currentState.laundryName.isBlank() -> {
                _state.update {it.copy(error = "Nama laundry tidak boleh kosong!")}
                return
            }
            currentState.role == "Staff" && currentState.selectedLaundry.isBlank() -> {
                _state.update { it.copy(error = "Silakan pilih laundry") }
                return
            }
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = authRepository.register(
                RegisterDto(
                    username = currentState.username,
                    password = currentState.password,
                    email = currentState.email,
                    confirmPassword = currentState.confirmPassword,
                    role = currentState.role,
                    laundryName = if (currentState.role == "Owner") currentState.laundryName else null,
                    selectedLaundry = if (currentState.role == "Staff") currentState.selectedLaundry else null
                )
            )

            result.fold(
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
                            error = error.message
                        )
                    }
                }
            )
        }
    }
}
