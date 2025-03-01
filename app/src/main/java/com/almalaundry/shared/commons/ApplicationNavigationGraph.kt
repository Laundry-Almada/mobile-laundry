package com.almalaundry.shared.commons

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.almalaundry.featured.auth.commons.AuthRoutes
import com.almalaundry.featured.auth.commons.authNavigation
import com.almalaundry.featured.home.commons.homeNavigation
import com.almalaundry.featured.order.commons.orderNavigation
import com.almalaundry.featured.profile.commons.profileNavigation

@Composable
fun ApplicationNavigationGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = AuthRoutes.Login.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
    ) {
        authNavigation()
        homeNavigation()
        orderNavigation()
        profileNavigation()
    }
}