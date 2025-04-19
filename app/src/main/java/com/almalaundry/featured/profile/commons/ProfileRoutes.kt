package com.almalaundry.featured.profile.commons

import kotlinx.serialization.Serializable

@Serializable
sealed class ProfileRoutes(val route: String) {
    @Serializable
    data object Index : ProfileRoutes("profile")

    @Serializable
    data object Edit : ProfileRoutes("edit-profile")
}