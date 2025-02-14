package com.almalaundry.featured.scan.commons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.almalaundry.featured.scan.presentation.screen.ScanScreen

fun NavGraphBuilder.scanNavigation() {
    composable(ScanRoutes.Index.route) {
        ScanScreen()
    }
}