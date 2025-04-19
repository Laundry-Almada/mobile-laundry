package com.almalaundry.featured.profile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.auth.data.repositories.AuthRepository
import com.almalaundry.featured.profile.data.repository.ProfileRepository
import com.almalaundry.featured.profile.presentation.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.almalaundry.featured.profile.data.model.UpdateProfileRequest

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
            authRepository.logout().fold(onSuccess = {
                _state.update { it.copy(isLoading = false, isLoggedOut = true) }
            }, onFailure = { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            })
        }
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
