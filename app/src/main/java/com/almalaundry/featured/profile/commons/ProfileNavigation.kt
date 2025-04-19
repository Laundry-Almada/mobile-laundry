package com.almalaundry.featured.profile.commons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.almalaundry.featured.profile.presentation.screen.EditProfileScreen
import com.almalaundry.featured.profile.presentation.screen.ProfileScreen
import com.almalaundry.shared.commons.compositional.LocalNavController

fun NavGraphBuilder.profileNavigation() {
    composable(ProfileRoutes.Index.route) {
        ProfileScreen()
    }

    composable(ProfileRoutes.Edit.route) {
        val navController = LocalNavController.current
        EditProfileScreen(navController = navController)
    }
}

//package com.almalaundry.featured.profile.commons
//
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.compose.composable
//import com.almalaundry.featured.profile.presentation.screen.ProfileScreen
//
//fun NavGraphBuilder.profileNavigation() {
//    composable(ProfileRoutes.Index.route) {
//        ProfileScreen()
//    }
//}