package com.example.zbudget.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zbudget.components.BottomBar
import com.example.zbudget.ui.home.HomeScreen

@Composable
fun AppNavBar(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route

    val showBottomBar = currentRoute in listOf(
        Screens.HOME,
        Screens.TRANSACTIONS,
        Screens.STAT,
        Screens.BUDGET
    )
    Scaffold(
        bottomBar = {
            if(showBottomBar) {
                BottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true}
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screens.HOME
        ) {
            composable(Screens.HOME) {
                HomeScreen()
            }
        }
    }
}