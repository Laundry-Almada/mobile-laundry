package com.almalaundry.featured.profile.presentation.state

data class ProfileState(
    val isLoading: Boolean = false,
    val error: String? = null,
    //  other state properties
)