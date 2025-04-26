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

    LaunchedEffect(state.name, state.email) {
        name = state.name
        email = state.email
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Edit Profile", style = MaterialTheme.typography.headlineSmall)

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
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Biarkan kosong jika tidak ingin mengubah") }
        )

        Button(
            onClick = {
                scope.launch {
                    viewModel.updateProfile(
                        name = name,
                        email = email,
                        password = password,
                        onSuccess = {
                            Toast.makeText(context, "Berhasil update profile", Toast.LENGTH_SHORT).show()
                            Log.d("EditProfile", "Navigating back to profile")
                            navController.navigate(ProfileRoutes.Index.route) {
                                popUpTo(ProfileRoutes.Edit.route) {
                                    inclusive = true
                                }
                            }
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
