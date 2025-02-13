package com.almalaundry.featured.order.commons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.almalaundry.featured.order.presentation.screen.OrderScreen

fun NavGraphBuilder.orderNavigation() {
    composable(OrderRoutes.Index.route) {
        OrderScreen()
    }
}