package com.almalaundry.featured.auth.presentation.screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.R
import com.almalaundry.featured.auth.commons.AuthRoutes
import com.almalaundry.featured.auth.presentation.viewmodels.RegisterViewModel
import com.almalaundry.featured.home.commons.HomeRoutes
import com.almalaundry.shared.commons.compositional.LocalNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val dactiveColor = colorResource(id = R.color.dactive)
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isRoleExpanded by remember { mutableStateOf(false) }
    var isLaundryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.navigate(HomeRoutes.Index.route) {
                popUpTo(AuthRoutes.Register.route) { inclusive = true }
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
                    text = "Register",
                    style = MaterialTheme.typography.headlineMedium.copy(color = dactiveColor),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = state.username,
                    onValueChange = viewModel::onUsernameChange,
                    label = { Text("Username", color = dactiveColor) },
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
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    label = { Text("Password", color = dactiveColor) },
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
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    label = { Text("Confirm Password", color = dactiveColor) },
                    textStyle = TextStyle(color = dactiveColor),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = dactiveColor,
                        unfocusedBorderColor = dactiveColor,
                        cursorColor = dactiveColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = isRoleExpanded,
                    onExpandedChange = { isRoleExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.role,
                        onValueChange = { },
                        label = { Text("Role", color = dactiveColor) },
                        textStyle = TextStyle(color = dactiveColor),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = dactiveColor,
                            unfocusedBorderColor = dactiveColor,
                            cursorColor = dactiveColor
                        ),
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = isRoleExpanded,
                        onDismissRequest = { isRoleExpanded = false }
                    ) {
                        listOf("Owner", "Staff").forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    viewModel.onRoleChange(role)
                                    isRoleExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.role == "Owner") {
                    OutlinedTextField(
                        value = state.laundryName,
                        onValueChange = viewModel::onLaundryNameChange,
                        label = { Text("Nama Laundry", color = dactiveColor) },
                        textStyle = TextStyle(color = dactiveColor),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = dactiveColor,
                            unfocusedBorderColor = dactiveColor,
                            cursorColor = dactiveColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = isLaundryExpanded,
                        onExpandedChange = { isLaundryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.selectedLaundry,
                            onValueChange = { },
                            label = { Text("Pilih Laundry", color = dactiveColor) },
                            textStyle = TextStyle(color = dactiveColor),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = dactiveColor,
                                unfocusedBorderColor = dactiveColor,
                                cursorColor = dactiveColor
                            ),
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLaundryExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = isLaundryExpanded,
                            onDismissRequest = { isLaundryExpanded = false }
                        ) {
                            state.availableLaundries.forEach { laundry ->
                                DropdownMenuItem(
                                    text = { Text(laundry) },
                                    onClick = {
                                        viewModel.onSelectedLaundryChange(laundry)
                                        isLaundryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(
                    onClick = viewModel::register,
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = dactiveColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Register")
                    }
                }

                TextButton(
                    onClick = { navController.navigate(AuthRoutes.Login.route) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Already have an account? Login", color = dactiveColor)
                }
            }
        }
    }
}
