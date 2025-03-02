package com.almalaundry.shared.commons

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.almalaundry.featured.auth.commons.AuthRoutes
import com.almalaundry.featured.auth.commons.authNavigation
import com.almalaundry.featured.home.commons.HomeRoutes
import com.almalaundry.featured.home.commons.homeNavigation
import com.almalaundry.featured.order.commons.orderNavigation
import com.almalaundry.featured.profile.commons.profileNavigation
import com.almalaundry.shared.commons.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun ApplicationNavigationGraph(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    val authViewModel = hiltViewModel<AuthViewModel>()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val session = authViewModel.sessionManager.getSession() // Pengecekan awal
        startDestination = if (session != null) HomeRoutes.Index.route else AuthRoutes.Login.route
    }

    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = navController,
            startDestination = startDestination!!,
            modifier = modifier,
            enterTransition = { slideInVertically { it } },
            popEnterTransition = { slideInVertically { -it } },
            exitTransition = { slideOutVertically { -it } },
            popExitTransition = { slideOutVertically { it } }) {
            authNavigation()
            homeNavigation()
            orderNavigation()
            profileNavigation()
        }
    }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel()