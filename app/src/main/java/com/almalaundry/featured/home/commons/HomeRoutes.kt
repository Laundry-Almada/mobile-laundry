package com.almalaundry.featured.home.commons

import kotlinx.serialization.Serializable

@Serializable
sealed class HomeRoutes(val route: String) {
    @Serializable
    data object Index : HomeRoutes("home")

    @Serializable
    data object Dashboard : HomeRoutes("dashboard")
}