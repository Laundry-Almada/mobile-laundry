package com.almalaundry.featured.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.auth.data.dtos.RegisterDto
import com.almalaundry.featured.auth.data.repositories.AuthRepository
import com.almalaundry.featured.auth.presentation.state.RegisterState
import com.almalaundry.featured.order.domain.models.Laundry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @HiltViewModel
// class RegisterViewModel @Inject constructor(
//    private val authRepository: AuthRepository
// ) : ViewModel() {
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
// }
//

// @HiltViewModel
// class RegisterViewModel @Inject constructor(
//    private val authRepository: AuthRepository
// ) : ViewModel() {
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
//            availableLaundries = if (value == "Staff") listOf("Laundry Almada", "Laundry Balmada")
// else emptyList(),
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
// }

@HiltViewModel
class RegisterViewModel
@Inject
constructor(
        private val authRepository: com.almalaundry.featured.auth.data.repositories.AuthRepository
) : ViewModel() {

    // Flow untuk menyimpan list laundry (gunakan model Laundry, bukan LaundryDto)
    private val _laundries = MutableStateFlow<List<Laundry>>(emptyList())
    //    val laundries: StateFlow<List<Laundry>> = _laundries

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onRoleChange(value: String) {
        _state.update {
            it.copy(
                    role = value,
                    availableLaundries = if (value == "Staff") listOf() else emptyList(),
                    selectedLaundry = if (value == "Staff") "" else it.selectedLaundry,
                    laundryName = if (value == "Owner") "" else it.laundryName
            )
        }

        if (value == "Staff") {
            getLaundries()
        }
    }

    private fun getLaundries() {
        viewModelScope.launch {
            val result = authRepository.getLaundries().getOrNull()
            if (result != null) {
                _laundries.value = result
                _state.update {
                    it.copy(availableLaundries = result.map { laundry -> laundry.name })
                }
            } else {
                _state.update { it.copy(error = "Gagal memuat data laundry") }
            }
        }
    }

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
                _state.update { it.copy(error = "Password tidak boleh kosong!") }
                return
            }
            currentState.password != currentState.confirmPassword -> {
                _state.update { it.copy(error = "Password dan Konfirmasi Password tidak cocok!") }
                return
            }
            currentState.role == "Owner" && currentState.laundryName.isBlank() -> {
                _state.update { it.copy(error = "Nama laundry tidak boleh kosong!") }
                return
            }
            currentState.role == "Staff" && currentState.selectedLaundry.isBlank() -> {
                _state.update { it.copy(error = "Silakan pilih laundry!") }
                return
            }
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val dto =
                    RegisterDto(
                            username = currentState.username,
                            password = currentState.password,
                            confirmPassword = currentState.confirmPassword,
                            email = currentState.email,
                            role = currentState.role,
                            laundryName =
                                    if (currentState.role == "Owner") currentState.laundryName
                                    else null,
                            selectedLaundry =
                                    if (currentState.role == "Staff") {
                                        // Ambil ID laundry dari nama yang dipilih
                                        _laundries.value
                                                .firstOrNull {
                                                    it.name == currentState.selectedLaundry
                                                }
                                                ?.id
                                    } else null
                    )

            val result = authRepository.register(dto)

            result.fold(
                    onSuccess = { _state.update { it.copy(isLoading = false, isSuccess = true) } },
                    onFailure = { error ->
                        _state.update {
                            it.copy(isLoading = false, error = error.message ?: "Registrasi gagal")
                        }
                    }
            )
        }
    }
}

// @HiltViewModel
// class RegisterViewModel @Inject constructor(
//    private val authRepository:
// com.almalaundry.featured.auth.data.repositories.AuthRepository<Any?>
// ) : ViewModel() {
//    private val _laundries = MutableStateFlow () <List<Laundry>(emptyList())
//    val laundries: StateFlow<List<Laundry>> = _laundries
//    private val _state = MutableStateFlow(RegisterState())
//    val state = _state.asStateFlow()
//
//
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
//    fun onRoleChange(value: String) {
//        _state.update {
//            it.copy(
//                role = value,
//                availableLaundries = if (value == "Staff") listOf("Laundry Almada", "Laundry
// Balmada") else emptyList(),
//                selectedLaundry = if (value == "Staff") "" else it.selectedLaundry,
//                laundryName = if (value == "Owner") "" else it.laundryName
//            )
//        }
//    }
//
//    fun onLaundryNameChange(laundryName: String) {
//        _state.update { it.copy(laundryName = laundryName) }
//    }
//
//    fun onSelectedLaundryChange(selectedLaundry: String) {
//        _state.update { it.copy(selectedLaundry = selectedLaundry) }
//    }

//    fun fetchLaundries() {
//        viewModelScope.launch {
//            when (val result = authRepository.getLaundries()) {
//                is Result.Success -> {
//                    _laundries.value = result.getOrNull() ?: emptyList()
//                }
//                is Result.Failure -> {
//                    // Tangani error, misal tampilkan toast
//                }
//            }
//        }
//    }

//    fun fetchAvailableLaundries() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true, error = null) }
//            val result = authRepository.getLaundries()
//            result.fold(
//                onSuccess = { laundries ->
//                    _laundries.value = laundries // Simpan objek Laundry ke state sendiri
//                    _state.update { it.copy(isLoading = false) }
//                },
//                onFailure = { error ->
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            error = error.message ?: "Gagal memuat data laundry"
//                        )
//                    }
//                }
//            )
//        }
//    }
//

//    fun fetchAvailableLaundries() {
//        viewModelScope.launch {
//            try {
//                val response = authRepository.getLaundries()
//                response.fold(
//                    onSuccess = { laundries ->
//                        _state.update {
//                            it.copy(
//                                availableLaundries = laundries.map { laundry -> laundry.name }, //
// hanya nama untuk ditampilkan
//                                isLoading = false
//                            )
//                        }
//                    },
//                    onFailure = { error ->
//                        _state.update {
//                            it.copy(
//                                error = error.message,
//                                isLoading = false
//                            )
//                        }
//                    }
//                )
//            } catch (e: Exception) {
//                _state.update { it.copy(error = e.message) }
//            }
//        }
//    }
//
//
//    fun register() {
//        val currentState = _state.value
//        when {
//            currentState.username.isBlank() -> {
//                _state.update { it.copy(error = "Username tidak boleh kosong!") }
//                return
//            }
//            currentState.email.isBlank() -> {
//                _state.update { it.copy(error = "Email tidak boleh kosong!") }
//                return
//            }
//            currentState.password.isBlank() -> {
//                _state.update {it.copy(error = "Password tidak boleh kosong")}
//                return
//            }
//            currentState.password != currentState.confirmPassword -> {
//                _state.update { it.copy(error = "Password dan Confirm Password harus sama!") }
//                return
//            }
//            currentState.role == "Owner" && currentState.laundryName.isBlank() -> {
//                _state.update {it.copy(error = "Nama laundry tidak boleh kosong!")}
//                return
//            }
//            currentState.role == "Staff" && currentState.selectedLaundry.isBlank() -> {
//                _state.update { it.copy(error = "Silakan pilih laundry") }
//                return
//            }
//        }
//
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//
//            val result = authRepository.register(
//                RegisterDto(
//                    username = currentState.username,
//                    password = currentState.password,
//                    email = currentState.email,
//                    confirmPassword = currentState.confirmPassword,
//                    role = currentState.role,
//                    laundryName = if (currentState.role == "Owner") currentState.laundryName else
// null,
//                    selectedLaundry = if (currentState.role == "Staff")
// currentState.selectedLaundry else null
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
// }
