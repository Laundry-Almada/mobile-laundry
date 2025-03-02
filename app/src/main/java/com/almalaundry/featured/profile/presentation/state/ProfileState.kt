package com.almalaundry.featured.profile.presentation.state

data class ProfileState(
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val error: String? = null,
    //  other state properties
)