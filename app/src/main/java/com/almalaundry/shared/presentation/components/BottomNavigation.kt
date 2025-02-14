package com.almalaundry.shared.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.composables.icons.lucide.Focus
import com.composables.icons.lucide.Lucide

@Composable
fun BottomNavigation(
    navController: NavController,
    items: List<NavigationItem> = NavigationItems.items,
    onScanClick: () -> Unit = {}
) {
    val softGray = Color.Gray.copy(alpha = 0.5f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        NavigationBar(
            modifier = Modifier
                .height(56.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEachIndexed { index, item ->
                val isSelected = currentRoute == item.route
                if (index == 2) {
                    NavigationBarItem(
                        icon = { },
                        label = { },
                        selected = false,
                        onClick = { },
                        modifier = Modifier.width(72.dp),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                } else {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(
                                        animateFloatAsState(
                                            targetValue = if (isSelected) 1.0f else 1f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        ).value
                                    )
                                    .fillMaxSize(),
                                tint = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    softGray
                            )
                        },
                        label = {
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(300)
                                ),
                                exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = tween(300)
                                )
                            ) {
                                Text(
                                    text = item.label,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                    modifier = Modifier.offset(y = (-4).dp)
                                )
                            }
                        },
                        selected = isSelected,
                        onClick = {
                            item.route?.let { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.offset(y = 4.dp)
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onScanClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(60.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation()
        ) {
            Icon(
                imageVector = Lucide.Focus,
                contentDescription = "Scan",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

