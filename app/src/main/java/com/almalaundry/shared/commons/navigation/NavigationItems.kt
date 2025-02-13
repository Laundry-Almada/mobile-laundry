package com.almalaundry.shared.commons.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.almalaundry.featured.home.commons.HomeRoutes
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Focus
import com.composables.icons.lucide.History
import com.composables.icons.lucide.LayoutDashboard
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingBag
import com.composables.icons.lucide.User

object NavigationItems {
    val items = listOf(
        NavigationItem(
            route = HomeRoutes.Dashboard.route,
            icon = Lucide.LayoutDashboard,
            label = "Home"
        ),
        NavigationItem(
            route = HomeRoutes.Orders.route,
            icon = Lucide.ShoppingBag,
            label = "Orders"
        ),
        NavigationItem(
            route = null,
            icon = Lucide.Focus,
            label = "Scan"
        ),
        NavigationItem(
            route = HomeRoutes.History.route,
            icon = Lucide.History,
            label = "History"
        ),
        NavigationItem(
            route = HomeRoutes.Profile.route,
            icon = Lucide.User,
            label = "Profile"
        )
    )
}

data class NavigationItem(
    val route: String? = null,
    val icon: ImageVector,
    val label: String
)
