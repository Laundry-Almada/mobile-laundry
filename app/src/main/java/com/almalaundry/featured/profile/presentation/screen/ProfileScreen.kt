package com.almalaundry.featured.profile.presentation.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.R
import com.almalaundry.featured.auth.commons.AuthRoutes
import com.almalaundry.featured.profile.commons.ProfileRoutes
import com.almalaundry.featured.profile.presentation.viewmodels.ProfileViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.presentation.components.BannerHeader
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            navController.navigate(AuthRoutes.Login.route) {
                popUpTo(ProfileRoutes.Index.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(state.updateMessage) {
        state.updateMessage?.let { message ->
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = if (state.isUpdateAvailable && state.updateApkUrl != null) "Unduh" else null,
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed && state.updateApkUrl != null) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.updateApkUrl))
                    context.startActivity(intent)
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box {
                        // BannerHeader tetap paling belakang
                        BannerHeader(
                            title = "Profil",
                            subtitle = "Kelola informasi akun Anda",
                            imageResId = R.drawable.header_basic2,
//          onBackClick = { navController.popBackStack() },
                            titleAlignment = Alignment.Start
                        )

                        // Gambar di atas banner, agak nindih
                        Image(
                            painter = painterResource(id = R.drawable.icon_profile),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(140.dp)
                                .align(Alignment.BottomCenter)
                                .offset(y = 130.dp) // makin besar makin turun
                        )
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (130).dp), // Mengatur agar kotak sedikit naik
                        shape = MaterialTheme.shapes.large, // Ujung kotak melengkung
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(25.dp)
                        ) {
                            Text(text = "Nama: ${state.name}")
                            Text(text = "Email: ${state.email}")
                            Text(text = "Role: ${state.role}")
                            Text(text = "Cabang Laundry: ${state.laundryName}")

                            Spacer(modifier = Modifier.height(16.dp))

//                             Tombol Edit Profile
                            Button(
                                onClick = { navController.navigate("edit-profile") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Edit Profile")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.checkForUpdate(context) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isCheckingUpdate
                            ) {
                                if (state.isCheckingUpdate) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Cek Pembaruan")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

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
                        }
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
//    LaunchedEffect(Unit) {
//        viewModel.loadProfileData()
//    }
//    val state by viewModel.state.collectAsState()
//    val navController = LocalNavController.current
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    // Navigasi ke layar login setelah logout berhasil
//    LaunchedEffect(state.isLoggedOut) {
//        if (state.isLoggedOut) {
//            navController.navigate(AuthRoutes.Login.route) {
//                popUpTo(ProfileRoutes.Index.route) { inclusive = true }
//            }
//        }
//    }
//    LaunchedEffect(state.updateMessage) {
//        state.updateMessage?.let { message ->
//            scope.launch {
//                val result = snackbarHostState.showSnackbar(
//                    message = message,
//                    actionLabel = if (state.isUpdateAvailable && state.updateApkUrl != null) "Unduh" else null,
//                    duration = SnackbarDuration.Long
//                )
//                if (result == SnackbarResult.ActionPerformed && state.updateApkUrl != null) {
//                    // Buka URL APK di browser
//                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.updateApkUrl))
//                    context.startActivity(intent)
//                }
//            }
//        }
//    }
//
//    Scaffold { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//
//            if (state.isLoading) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            } else {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Profile",
//                        style = MaterialTheme.typography.headlineSmall
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Tampilkan informasi user
//                    Text(text = "Nama: ${state.name}")
//                    Text(text = "Email: ${state.email}")
//                    Text(text = "Role: ${state.role}")
//                    Text(text = "Cabang Laundry: ${state.laundryName}")
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    // Tombol Edit Profile
//                    Button(
//                        onClick = { navController.navigate("edit-profile") },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("Edit Profile")
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Tombol Cek Pembaruan
//                    Button(
//                        onClick = { viewModel.checkForUpdate(context) },
//                        modifier = Modifier.fillMaxWidth(),
//                        enabled = !state.isCheckingUpdate
//                    ) {
//                        if (state.isCheckingUpdate) {
//                            CircularProgressIndicator(
//                                modifier = Modifier.size(24.dp),
//                                color = MaterialTheme.colorScheme.onPrimary
//                            )
//                        } else {
//                            Text("Cek Pembaruan")
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Tombol Logout
//                    Button(
//                        onClick = { viewModel.logout() },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.error,
//                            contentColor = MaterialTheme.colorScheme.onError
//                        )
//                    ) {
//                        Text("Logout")
//                    }
//
//                    // Tampilkan error jika ada
//                    if (state.error != null) {
//                        Text(
//                            text = state.error ?: "Unknown error",
//                            color = MaterialTheme.colorScheme.error,
//                            modifier = Modifier.padding(top = 8.dp)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

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