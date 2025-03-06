package com.almalaundry.featured.auth.presentation.state

data class LoginState(
    val username: String = "admin",
    val password: String = "admin",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)