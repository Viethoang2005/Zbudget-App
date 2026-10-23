package com.example.zbudget.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.zbudget.navigation.Screens.TRANSACTION_DETAIL
import com.example.zbudget.ui.components.BottomBar
import com.example.zbudget.ui.home.HomeScreen
import com.example.zbudget.ui.transactions.TransactionDetailScreen
import com.example.zbudget.ui.transactions.TransactionsScreen
import com.example.zbudget.ui.transactions.TransactionsViewModel

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
                            popUpTo(navController.graph.startDestinationId)
                            { inclusive = true}
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
                HomeScreen(
                    onNavigateToTransactions = {
                        navController.navigate(Screens.TRANSACTIONS)
                    },
                    onItemClick = { transactionId ->
                        navController.navigate(
                            "${TRANSACTION_DETAIL}/$transactionId"
                        )
                    }
                )
            }

            composable(Screens.TRANSACTIONS) {
                TransactionsScreen(
                    onTransactionClick = { transactionId ->
                        navController.navigate(
                            "transaction_detail/$transactionId"
                        )
                    }
                )
            }

            composable(
                route = "${TRANSACTION_DETAIL}/{transactionId}",
                arguments = listOf(
                    navArgument("transactionId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->
                val transactionId =
                    entry.arguments?.getString("transactionId")

                val viewModel: TransactionsViewModel = hiltViewModel()
                val transaction = transactionId?.let {
                    viewModel.getTransactionById(it)
                }

                if (transaction != null) {
                    TransactionDetailScreen(
                        transaction = transaction,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                } else {
                    Text(
                        text = "Không tìm thấy giao dịch."
                    )
                }
            }
        }
    }
}