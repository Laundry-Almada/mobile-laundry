package com.almalaundry.featured.auth.presentation.state

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: String = "owner",
    val laundryName: String = "",
    val laundryAddress: String = "",
    val laundryPhone: String = "",
    val selectedLaundry: String = "",
    val availableLaundries: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)