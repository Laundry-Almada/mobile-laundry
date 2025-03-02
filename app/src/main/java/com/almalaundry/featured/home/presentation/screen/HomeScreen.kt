package com.almalaundry.featured.home.presentation.screen

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.almalaundry.featured.home.commons.HomeRoutes
import com.almalaundry.featured.order.commons.OrderRoutes
import com.almalaundry.featured.order.presentation.screen.HistoryOrderScreen
import com.almalaundry.featured.order.presentation.screen.OrderScreen
import com.almalaundry.featured.order.presentation.screen.ScanScreen
import com.almalaundry.featured.profile.commons.ProfileRoutes
import com.almalaundry.featured.profile.presentation.screen.ProfileScreen
import com.almalaundry.shared.commons.compositional.LocalHomeNavController
import com.almalaundry.shared.presentation.components.BottomNavigation

@Composable
fun HomeScreen() {
    val homeNavController = rememberNavController()

    CompositionLocalProvider(LocalHomeNavController provides homeNavController) {
        Scaffold(contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(sides = WindowInsetsSides.Bottom),
            bottomBar = {
                BottomNavigation(navController = homeNavController)
            }) { paddingValues ->
            HomeNavGraph(
                navController = homeNavController,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun HomeNavGraph(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    NavHost(navController = navController,
        startDestination = HomeRoutes.Dashboard.route,
        modifier = modifier,
        enterTransition = { fadeIn() + slideInVertically() },
        popEnterTransition = { fadeIn() + slideInVertically() },
        exitTransition = { fadeOut() + slideOutVertically() },
        popExitTransition = { fadeOut() + slideOutVertically() }) {
        composable(HomeRoutes.Dashboard.route) {
            DashboardUser()
        }
        composable(OrderRoutes.Index.route) {
            OrderScreen()
        }
        composable(OrderRoutes.Scan.route) {
            ScanScreen()
        }
        composable(OrderRoutes.History.route) {
            HistoryOrderScreen()
        }
        composable(ProfileRoutes.Index.route) {
            ProfileScreen()
        }
    }
}