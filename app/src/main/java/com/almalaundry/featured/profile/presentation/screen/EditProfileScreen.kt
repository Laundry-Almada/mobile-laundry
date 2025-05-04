package com.almalaundry.featured.profile.presentation.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.almalaundry.featured.profile.commons.ProfileRoutes
import com.almalaundry.featured.profile.presentation.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.almalaundry.R
import com.almalaundry.shared.presentation.components.BannerHeader
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide


@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.name, state.email) {
        name = state.name
        email = state.email
    }
    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box {
                // Banner Header
                BannerHeader(
                    title = "Edit Profile",
                    subtitle = "Perbarui informasi akun Anda",
                    imageResId = R.drawable.header_basic2,
                    titleAlignment = Alignment.Start
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
//                Text(text = "Edit Profile", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = if (password.isNotEmpty()) {
                            when {
                                password.length < 8 -> "Minimal 8 karakter"
                                !password.any { it.isLetter() } -> "Harus ada huruf"
                                !password.any { it.isDigit() } -> "Harus ada angka"
                                else -> null
                            }
                        } else null
                    },
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) Text(
                            passwordError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Biarkan kosong jika tidak ingin mengubah") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Lucide.Eye else Lucide.EyeOff
                        val description =
                            if (passwordVisible) "Sembunyikan password" else "Tampilkan password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    }
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError =
                            if (password != confirmPassword) "Konfirmasi tidak sesuai" else null
                    },
                    isError = confirmPasswordError != null,
                    supportingText = {
                        if (confirmPasswordError != null) Text(
                            confirmPasswordError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    label = { Text("Konfirmasi Password") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ulangi password jika ingin mengubah") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Lucide.Eye else Lucide.EyeOff
                        val description =
                            if (confirmPasswordVisible) "Sembunyikan password" else "Tampilkan password"

                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    }
                )

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.updateProfile(
                                name = name,
                                email = email,
                                password = password,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Berhasil update profile",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("EditProfile", "Navigating back to profile")
                                    navController.popBackStack()
                                },
                                onError = {
                                    Log.e("EditProfile", "Error update profile: $it")
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

//package com.almalaundry.featured.profile.presentation.screen
//
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.almalaundry.featured.profile.commons.ProfileRoutes
//import com.almalaundry.featured.profile.presentation.viewmodels.ProfileViewModel
//import kotlinx.coroutines.launch
//
//@Composable
//fun EditProfileScreen(
//    navController: NavController,
//    viewModel: ProfileViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
////    // Local state untuk input
////    var name by remember { mutableStateOf(state.name ?: "") }
////    var email by remember { mutableStateOf(state.email ?: "") }
////    var password by remember { mutableStateOf("") }
////    var laundryName by remember { mutableStateOf(state.laundryName ?: "") }
//
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var laundryName by remember { mutableStateOf("") }
//
//    LaunchedEffect(state.name, state.email, state.laundryName) {
//        name = state.name
//        email = state.email
//        laundryName = state.laundryName
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        Text(text = "Edit Profile", style = MaterialTheme.typography.headlineSmall)
//
//        OutlinedTextField(
//            value = name,
//            onValueChange = { name = it },
//            label = { Text("Nama") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth(),
//            placeholder = { Text("Biarkan kosong jika tidak ingin mengubah") }
//        )
//
////        OutlinedTextField(
////            value = laundryName,
////            onValueChange = { laundryName = it },
////            label = { Text("Cabang Laundry") },
////            modifier = Modifier.fillMaxWidth()
////        )
//
//        Button(
//            onClick = {
//                scope.launch {
//                    viewModel.updateProfile(
//                        name = name,
//                        email = email,
//                        password = password,
//                        laundryName = laundryName,
//                        onSuccess = {
//                            Toast.makeText(context, "Berhasil update profile", Toast.LENGTH_SHORT).show()
//                            Log.d("EditProfile", "Navigating back to profile")
////                            navController.popBackStack() // kembali ke profile
//                            navController.navigate(ProfileRoutes.Index.route) {
//                                popUpTo(ProfileRoutes.Index.route) {
//                                    inclusive = true
//                                }
//                            }
//                        },
//                        onError = {
//                            Log.e("EditProfile", "Error update profile: $it")
//                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//                            // Bisa tampilkan Snackbar, Toast, dll
//                            // Misalnya:
//                            // Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Save")
//        }
//    }
//}
