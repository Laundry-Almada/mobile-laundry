package com.almalaundry.featured.home.presentation.state

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    //  other state properties
)
