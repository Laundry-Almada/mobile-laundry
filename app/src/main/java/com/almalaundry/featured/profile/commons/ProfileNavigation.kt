package com.almalaundry.featured.profile.commons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.almalaundry.featured.profile.presentation.screen.ProfileScreen

fun NavGraphBuilder.profileNavigation() {
    composable(ProfileRoutes.Index.route) {
        ProfileScreen()
    }
}