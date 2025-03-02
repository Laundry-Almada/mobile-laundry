package com.almalaundry.featured.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.auth.data.dtos.LoginRequest
import com.almalaundry.featured.auth.data.repositories.AuthRepository
import com.almalaundry.featured.auth.presentation.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = authRepository.login(
                LoginRequest(
                    email = state.value.email, password = state.value.password
                )
            )

            result.fold(onSuccess = { authData ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        token = authData.token,
                        name = authData.name
                    )
                }
            }, onFailure = { error ->
                _state.update {
                    it.copy(
                        isLoading = false, error = error.message
                    )
                }
            })
        }
    }
}




