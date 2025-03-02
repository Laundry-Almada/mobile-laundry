package com.almalaundry.shared.presentation.state

sealed class SplashState {
    data object Loading : SplashState()
    data object NavigateToDashboard : SplashState()
    data object NavigateToLogin : SplashState()
}