package com.almalaundry.featured.auth.presentation.state

data class LoginState(
//    val username: String = "admin",
//    val password: String = "admin",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val token: String? = null,
    val name: String? = null,
    val error: String? = null
)