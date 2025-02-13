package com.almalaundry.featured.history.commons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.almalaundry.featured.history.presentation.screen.HistoryScreen

fun NavGraphBuilder.historyNavigation() {
    composable(HistoryRoutes.Index.route) {
        HistoryScreen()
    }
}