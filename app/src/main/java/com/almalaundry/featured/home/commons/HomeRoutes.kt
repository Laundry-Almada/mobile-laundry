package com.almalaundry.featured.home.commons

import kotlinx.serialization.Serializable

@Serializable
sealed class HomeRoutes(val route: String) {
    @Serializable
    data object Index : HomeRoutes("home")

    @Serializable
    data object Dashboard : HomeRoutes("dashboard")

    @Serializable
    data object Orders : HomeRoutes("orders")

    @Serializable
    data object Scan : HomeRoutes("scan")

    @Serializable
    data object Profile : HomeRoutes("profile")

    @Serializable
    data object History : HomeRoutes("history")
}