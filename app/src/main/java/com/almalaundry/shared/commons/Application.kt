package com.almalaundry.shared.commons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.presentation.components.BottomNavigation
import com.almalaundry.shared.presentation.ui.theme.AlmaLaundryTheme

@Composable
fun Application() {
    AlmaLaundryTheme {
        Surface {
            val navController = rememberNavController()

            CompositionLocalProvider (LocalNavController provides navController) {
                Scaffold(
                    bottomBar = {
                        BottomNavigation(navController = navController)
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        ApplicationNavigationGraph(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}