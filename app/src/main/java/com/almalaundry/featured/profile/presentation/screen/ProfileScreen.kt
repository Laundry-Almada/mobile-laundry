package com.almalaundry.featured.profile.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.featured.auth.commons.AuthRoutes
import com.almalaundry.featured.profile.commons.ProfileRoutes
import com.almalaundry.featured.profile.presentation.viewmodels.ProfileViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController

//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.almalaundry.featured.auth.commons.AuthRoutes
//import com.almalaundry.featured.profile.commons.ProfileRoutes
//import com.almalaundry.featured.profile.presentation.viewmodels.ProfileViewModel
//import com.almalaundry.shared.commons.compositional.LocalNavController

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    // Navigasi ke layar login setelah logout berhasil
    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            navController.navigate(AuthRoutes.Login.route) {
                popUpTo(ProfileRoutes.Index.route) { inclusive = true }
            }
        }
    }

    Scaffold { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tampilkan informasi user
                    Text(text = "Nama: ${state.name}")
                    Text(text = "Email: ${state.email}")
                    Text(text = "Role: ${state.role}")
                    Text(text = "Cabang Laundry: ${state.laundryName}")

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tombol Logout
                    Button(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Logout")
                    }

                    if (state.error != null) {
                        Text(
                            text = state.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Button(onClick = {
                        navController.navigate("edit-profile")
                    }) {
                        Text("Edit Profile")
                    }
                }
            }
        }
    }
}

//@Composable
//fun ProfileScreen(
//    viewModel: ProfileViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//    val navController = LocalNavController.current
//
//    // Navigasi ke layar login setelah logout berhasil
//    LaunchedEffect(state.isLoggedOut) {
//        if (state.isLoggedOut) {
//            navController.navigate(AuthRoutes.Login.route) {
//                popUpTo(ProfileRoutes.Index.route) { inclusive = true }
//            }
//        }
//    }
//
//    Scaffold { paddingValues ->
//        Box(modifier = Modifier.padding(paddingValues)) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Profile Screen", style = MaterialTheme.typography.bodyMedium
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Tombol Logout
//                Button(
//                    onClick = { viewModel.logout() },
//                    enabled = !state.isLoading,
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.error,
//                        contentColor = MaterialTheme.colorScheme.onError
//                    )
//                ) {
//                    if (state.isLoading) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(24.dp),
//                            color = MaterialTheme.colorScheme.onError
//                        )
//                    } else {
//                        Text("Logout")
//                    }
//                }
//
//                if (state.error != null) {
//                    Text(
//                        text = state.error ?: "Unknown error",
//                        color = MaterialTheme.colorScheme.error,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//                }
//            }
//        }
//    }
//}