package com.almalaundry.featured.profile.presentation.state

data class ProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val laundryName: String = "",
    val isLoggedOut: Boolean = false,
    val error: String? = null,
    val isCheckingUpdate: Boolean = false,
    val updateMessage: String? = null,
    val updateApkUrl: String? = null,
    val isUpdateAvailable: Boolean = false
)