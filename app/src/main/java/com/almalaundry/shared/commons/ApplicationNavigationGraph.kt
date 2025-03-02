package com.almalaundry.shared.commons

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.almalaundry.featured.auth.commons.AuthRoutes
import com.almalaundry.featured.auth.commons.authNavigation
import com.almalaundry.featured.auth.data.repositories.AuthRepository
import com.almalaundry.featured.home.commons.HomeRoutes
import com.almalaundry.featured.home.commons.homeNavigation
import com.almalaundry.featured.order.commons.orderNavigation
import com.almalaundry.featured.profile.commons.profileNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun ApplicationNavigationGraph(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    val authRepository =
        hiltViewModel<AuthViewModel>().authRepository // Ambil AuthRepository via ViewModel
    var startDestination by remember { mutableStateOf(AuthRoutes.Login.route) }

    // Cek token saat pertama kali aplikasi dibuka
    LaunchedEffect(Unit) {
        val token = authRepository.getToken()
        startDestination = if (token != null) HomeRoutes.Index.route else AuthRoutes.Login.route
    }

    NavHost(navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { slideInHorizontally { it } },
        popEnterTransition = { slideInHorizontally { -it } },
        exitTransition = { slideOutHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } }) {
        authNavigation()
        homeNavigation()
        orderNavigation()
        profileNavigation()
    }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel()