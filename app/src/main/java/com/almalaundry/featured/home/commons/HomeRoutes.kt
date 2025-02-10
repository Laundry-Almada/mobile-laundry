package com.almalaundry.featured.home.commons

import kotlinx.serialization.Serializable

@Serializable
sealed class HomeRoutes(val route: String) {
    @Serializable
    data object Index : HomeRoutes("home")

//    data object Detail : HomeRoutes("detail")
}