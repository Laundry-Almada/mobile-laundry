package com.almalaundry.featured.auth.commons

import kotlinx.serialization.Serializable

@Serializable
sealed class AuthRoutes(val route: String) {
    @Serializable
    data object Login : AuthRoutes("login")

    @Serializable
    data object Register : AuthRoutes("register")
}