package com.almalaundry.featured.home.commons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.almalaundry.featured.home.presentation.screen.HomeScreen

fun NavGraphBuilder.homeNavigation() {
    composable(route = HomeRoutes.Index.route) {
        HomeScreen()
    }
}
