package com.almalaundry.shared.commons

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.presentation.ui.theme.AlmaLaundryTheme

@Composable
fun Application() {
    AlmaLaundryTheme {
        Surface {
            val navController = rememberNavController()

            CompositionLocalProvider(LocalNavController provides navController) {
                ApplicationNavigationGraph(
                    navController = navController
                )
            }
        }
    }
}