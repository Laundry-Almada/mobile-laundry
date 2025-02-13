package com.almalaundry.featured.history.presentation.state

data class HistoryState(
    val isLoading: Boolean = false,
    val error: String? = null,
    //  other state properties
)

