package com.example.zbudget.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
private val bottomNavItem = listOf(
    BottomNavItem(
        route = Screens.HOME,
        label = "Trang Chủ",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        route = Screens.TRANSACTIONS,
        label = "Giao dịch",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        route = Screens.STAT,
        label = "Thống Kê",
        icon = Icons.Default.StackedBarChart
    ),
    BottomNavItem(
        route = Screens.BUDGET,
        label = "Ngân sách",
        icon = Icons.Default.Person
    ),
)

@Composable
fun BottomBar(
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = ZbudgetCream,
        contentColor = ZbudgetSecondary
    ) {
        bottomNavItem.forEach { item ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { destination ->
                    destination.route == item.route
                } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    onNavigate(item.route)
                },
                icon = {
                    Icon(imageVector = item.icon,
                        contentDescription = item.label)
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp
                    )
                },
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
