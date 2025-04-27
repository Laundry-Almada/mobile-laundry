package com.almalaundry.featured.auth.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.R
import com.almalaundry.featured.auth.commons.AuthRoutes
import com.almalaundry.featured.auth.presentation.viewmodels.LoginViewModel
import com.almalaundry.featured.home.commons.HomeRoutes
import com.almalaundry.shared.commons.compositional.LocalNavController

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    val dactiveColor = colorResource(id = R.color.dactive) // 🔥 warna dari colors.xml

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.navigate(HomeRoutes.Index.route) {
                popUpTo(AuthRoutes.Login.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.laundry),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(16.dp))
                .align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineMedium.copy(color = dactiveColor),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email", color = dactiveColor) },
                    textStyle = TextStyle(color = dactiveColor),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = dactiveColor,
                        unfocusedBorderColor = dactiveColor,
                        cursorColor = dactiveColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Password", color = dactiveColor) },
                    textStyle = TextStyle(color = dactiveColor),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = dactiveColor,
                        unfocusedBorderColor = dactiveColor,
                        cursorColor = dactiveColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(
                    onClick = viewModel::login,
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = dactiveColor, // ⬅️ ini untuk background button
                        contentColor = Color.White // ⬅️ ini untuk warna teks/icon di dalam button
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }

                Text(
                    text = "Masuk sebagai Customer",
                    color = dactiveColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
                        .clickable {
                            navController.navigate(HomeRoutes.CustomerDashboard.route)
                        }
                )

                Text(
                    text = "Belum punya akun? Daftar",
                    color = dactiveColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable {
                            navController.navigate(AuthRoutes.Register.route)
                        }
                )
            }
        }
    }
}
//package com.almalaundry.featured.auth.presentation.screen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.almalaundry.R
//import com.almalaundry.featured.auth.commons.AuthRoutes
//import com.almalaundry.featured.auth.presentation.viewmodels.LoginViewModel
//import com.almalaundry.featured.home.commons.HomeRoutes
//import com.almalaundry.shared.commons.compositional.LocalNavController
//
//@Composable
//fun LoginScreen(
//    viewModel: LoginViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//    val navController = LocalNavController.current
//
//    LaunchedEffect(state.isSuccess) {
//        if (state.isSuccess) {
//            navController.navigate(HomeRoutes.Index.route) {
//                popUpTo(AuthRoutes.Login.route) { inclusive = true }
//            }
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.laundry),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "Login",
//                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier.padding(bottom = 32.dp)
//            )
//
//            OutlinedTextField(
//                value = state.email,
//                onValueChange = viewModel::onEmailChange,
//                label = { Text("Email") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = state.password,
//                onValueChange = viewModel::onPasswordChange,
//                label = { Text("Password") },
//                visualTransformation = PasswordVisualTransformation(),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            if (state.error != null) {
//                Text(
//                    text = state.error!!,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//            }
//
//            Button(
//                onClick = viewModel::login,
//                enabled = !state.isLoading,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 24.dp)
//            ) {
//                if (state.isLoading) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(24.dp),
//                        color = MaterialTheme.colorScheme.onPrimary
//                    )
//                } else {
//                    Text("Login")
//                }
//            }
//
//            TextButton(
//                onClick = { navController.navigate(AuthRoutes.Register.route) },
//                modifier = Modifier.padding(top = 8.dp)
//            ) {
//                Text("Don't have an account? Register")
//            }
//        }
//    }
//}
