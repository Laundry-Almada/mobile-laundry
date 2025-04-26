package com.almalaundry.featured.order.commons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.almalaundry.featured.order.presentation.screen.CreateOrderScreen
import com.almalaundry.featured.order.presentation.screen.DetailOrderScreen
import com.almalaundry.featured.order.presentation.screen.HistoryOrderScreen
import com.almalaundry.featured.order.presentation.screen.OrderScreen
import com.almalaundry.featured.order.presentation.screen.PrintScreen
import com.almalaundry.featured.order.presentation.screen.ScanScreen

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
    composable<OrderRoutes.Print> {
        val data = it.toRoute<OrderRoutes.Print>()
        PrintScreen(
            barcode = data.barcode,
            customerName = data.customerName,
            serviceName = data.serviceName,
            weight = data.weight,
            totalPrice = data.totalPrice,
            createdAt = data.createdAt
        )
    }
    composable(OrderRoutes.Scan.route) {
        ScanScreen()
    }
    composable(OrderRoutes.History.route) {
        HistoryOrderScreen()
    }
}