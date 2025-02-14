package com.almalaundry.shared.commons

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.almalaundry.featured.history.commons.historyNavigation
import com.almalaundry.featured.home.commons.HomeRoutes
import com.almalaundry.featured.home.commons.homeNavigation
import com.almalaundry.featured.order.commons.orderNavigation
import com.almalaundry.featured.profile.commons.profileNavigation
import com.almalaundry.featured.scan.commons.scanNavigation

@Composable
fun ApplicationNavigationGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoutes.Index.route,
    ) {
        homeNavigation()
        orderNavigation()
        scanNavigation()
        historyNavigation()
        profileNavigation()
    }
}