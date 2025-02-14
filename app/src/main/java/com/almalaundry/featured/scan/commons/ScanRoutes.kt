package com.almalaundry.featured.scan.commons

import kotlinx.serialization.Serializable

@Serializable
sealed class ScanRoutes(val route: String) {
    @Serializable
    data object Index : ScanRoutes("scan")
}