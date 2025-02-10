package com.almalaundry.shared.commons.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.almalaundry.featured.history.commons.HistoryRoutes
import com.almalaundry.featured.home.commons.HomeRoutes
import com.almalaundry.featured.order.commons.OrderRoutes
import com.almalaundry.featured.profile.commons.ProfileRoutes
import com.composables.icons.lucide.History
import com.composables.icons.lucide.LayoutDashboard
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingBag
import com.composables.icons.lucide.User

object NavigationItems {
    val items = listOf(
        NavigationItem(
            route = HomeRoutes.Index.route,
            icon = Lucide.LayoutDashboard,
            label = "Home"
        ),
        NavigationItem(
//            route = OrderRoutes.Index.route,
            icon = Lucide.ShoppingBag,
            label = "Orders"
        ),
        NavigationItem(
//            route = HistoryRoutes.Index.route,
            icon = Lucide.History,
            label = "History"
        ),
        NavigationItem(
//            route = ProfileRoutes.Index.route,
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
