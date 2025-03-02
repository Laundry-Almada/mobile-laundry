package com.almalaundry.shared.commons

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.almalaundry.featured.auth.commons.AuthRoutes
import com.almalaundry.featured.home.commons.HomeRoutes
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.presentation.screen.SplashScreen
import com.almalaundry.shared.presentation.state.SplashState
import com.almalaundry.shared.presentation.ui.theme.AlmaLaundryTheme
import com.almalaundry.shared.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun Application() {
    AlmaLaundryTheme {
        Surface {
            val navController = rememberNavController()
            val authViewModel = hiltViewModel<AuthViewModel>()
            var splashState by remember { mutableStateOf<SplashState>(SplashState.Loading) }

            LaunchedEffect(Unit) {
                val session = authViewModel.sessionManager.getSession()
                delay(1000)
                splashState = if (session != null) {
                    SplashState.NavigateToDashboard
                } else {
                    SplashState.NavigateToLogin
                }
            }

            when (splashState) {
                SplashState.Loading -> SplashScreen()
                is SplashState.NavigateToDashboard -> {
                    CompositionLocalProvider(LocalNavController provides navController) {
                        ApplicationNavigationGraph(
                            navController = navController, startDestination = HomeRoutes.Index.route
                        )
                    }
                }

                is SplashState.NavigateToLogin -> {
                    CompositionLocalProvider(LocalNavController provides navController) {
                        ApplicationNavigationGraph(
                            navController = navController, startDestination = AuthRoutes.Login.route
                        )
                    }
                }
            }
        }
    }
}
