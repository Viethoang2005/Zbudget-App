
package com.example.zbudget.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.zbudget.navigation.Screens
import com.example.zbudget.ui.theme.ZbudgetCream
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetSecondary
import com.example.zbudget.ui.theme.ZbudgetText

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(
        route = Screens.HOME,
        label = "Trang Chủ",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        route = Screens.TRANSACTIONS,
        label = "Giao dịch",
        icon = Icons.Default.Receipt
    ),
    BottomNavItem(
        route = Screens.STAT,
        label = "Thống Kê",
        icon = Icons.Default.StackedBarChart
    ),
    BottomNavItem(
        route = Screens.BUDGET,
        label = "Ngân sách",
        icon = Icons.Default.AccountBalanceWallet
    ),
    BottomNavItem(
        route = Screens.PROFILE,
        label = "Tài khoản",
        icon = Icons.Default.AccountBox
    )
)

@Composable
fun BottomBar(
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = ZbudgetCream,
        contentColor = ZbudgetSecondary,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { destination ->
                    destination.route == item.route
                } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ZbudgetGreen,
                    selectedTextColor = ZbudgetText,
                    indicatorColor = ZbudgetGreenLight,
                    unselectedIconColor = Color(0xFF8A948D),
                    unselectedTextColor = Color(0xFF8A948D)
                )
            )
        }
    }
}


@Composable
fun NavigationRailBar(
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    NavigationRail(
        containerColor = ZbudgetCream
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { destination ->
                    destination.route == item.route
                } == true

            NavigationRailItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = ZbudgetGreen,
                    selectedTextColor = ZbudgetText,
                    indicatorColor = ZbudgetGreenLight,
                    unselectedIconColor = Color(0xFF8A948D),
                    unselectedTextColor = Color(0xFF8A948D)
                )
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    BottomBar(
        currentDestination = null,
        onNavigate = {}
    )
}