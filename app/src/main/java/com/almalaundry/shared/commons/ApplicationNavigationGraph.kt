package com.almalaundry.shared.commons

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.almalaundry.featured.auth.commons.authNavigation
import com.almalaundry.featured.home.commons.HomeRoutes
import com.almalaundry.featured.home.commons.homeNavigation
import com.almalaundry.featured.home.presentation.screen.CustomerDashboardScreen
import com.almalaundry.featured.order.commons.orderNavigation
import com.almalaundry.featured.profile.commons.profileNavigation

@Composable
fun ApplicationNavigationGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        authNavigation()
        homeNavigation()
        composable(HomeRoutes.CustomerDashboard.route) {
            CustomerDashboardScreen()
        }
        orderNavigation()
        profileNavigation()
    }
}