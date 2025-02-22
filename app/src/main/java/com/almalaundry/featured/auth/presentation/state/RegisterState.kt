package com.almalaundry.featured.auth.presentation.state

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: String = "Owner", // Default role
    val laundryName: String = "", // Untuk owner
    val selectedLaundry: String = "", // Untuk staff
    val availableLaundries: List<String> = listOf("Laundry A", "Laundry B", "Laundry C"), // Daftar laundry untuk staff
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
