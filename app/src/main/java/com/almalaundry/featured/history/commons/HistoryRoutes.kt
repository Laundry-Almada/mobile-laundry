package com.almalaundry.featured.history.commons

import kotlinx.serialization.Serializable

@Serializable
sealed class HistoryRoutes(val route: String) {
    @Serializable
    data object Index : HistoryRoutes("history")
}