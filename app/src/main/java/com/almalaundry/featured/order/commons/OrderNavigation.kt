package com.almalaundry.featured.order.commons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.almalaundry.featured.order.presentation.screen.CreateOrderScreen
import com.almalaundry.featured.order.presentation.screen.DetailOrderScreen
import com.almalaundry.featured.order.presentation.screen.OrderScreen

fun NavGraphBuilder.orderNavigation() {
    composable<OrderRoutes.Index> {
        OrderScreen()
    }
    composable<OrderRoutes.Create> {
        CreateOrderScreen()
    }
    composable<OrderRoutes.Detail> {
        val data = it.toRoute<OrderRoutes.Detail>()
        DetailOrderScreen(orderId = data.orderId)
    }

}