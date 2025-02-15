package com.almalaundry.featured.auth.commons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.almalaundry.featured.auth.presentation.screen.LoginScreen
import com.almalaundry.featured.auth.presentation.screen.RegisterScreen

fun NavGraphBuilder.authNavigation() {
    composable(AuthRoutes.Login.route) {
        LoginScreen()
    }
    composable(AuthRoutes.Register.route) {
        RegisterScreen()
    }
}