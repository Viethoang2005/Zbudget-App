
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
import com.example.zbudget.ui.auth.AuthViewModel
import com.example.zbudget.ui.auth.LoginScreen
import com.example.zbudget.ui.auth.RegisterScreen
import com.example.zbudget.ui.budget.BudgetScreen
import com.example.zbudget.ui.components.BottomBar
import com.example.zbudget.ui.home.HomeScreen
import com.example.zbudget.ui.statistics.StatisticsScreen
import com.example.zbudget.ui.transactions.AddEditTransactionScreen
import com.example.zbudget.ui.transactions.TransactionDetailScreen
import com.example.zbudget.ui.transactions.TransactionsScreen
import com.example.zbudget.ui.transactions.TransactionsViewModel

@Composable
fun AppNavBar() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
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
            if (showBottomBar) {
                BottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screens.HOME) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screens.LOGIN
        ) {
            composable(Screens.LOGIN) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screens.REGISTER)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screens.HOME) {
                            popUpTo(Screens.LOGIN) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screens.REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screens.LOGIN)
                    },
                    onRegisterSuccess = {
                        navController.navigate(Screens.LOGIN) {
                            popUpTo(Screens.REGISTER) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screens.HOME) {
                HomeScreen(
                    onNavigateToTransactions = {
                        navController.navigate(Screens.TRANSACTIONS)
                    },
                    onItemClick = { transactionId ->
                        navController.navigate(
                            "$TRANSACTION_DETAIL/$transactionId"
                        )
                    },
                    onLogOut = {
                        authViewModel.logout()

                        navController.navigate(Screens.LOGIN) {
                            popUpTo(Screens.HOME) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screens.TRANSACTIONS) {
                TransactionsScreen(
                    onAddClick = {
                        navController.navigate("addTransaction")
                    },
                    onTransactionClick = { transactionId ->
                        navController.navigate(
                            "$TRANSACTION_DETAIL/$transactionId"
                        )
                    }
                )
            }

            composable(Screens.ADD_TRANSACTION) {
                AddEditTransactionScreen(
                    transactionId = null,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "${Screens.EDIT_TRANSACTION}/{transactionId}",
                arguments = listOf(
                    navArgument("transactionId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments
                    ?.getString("transactionId")

                AddEditTransactionScreen(
                    transactionId = transactionId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "$TRANSACTION_DETAIL/{transactionId}",
                arguments = listOf(
                    navArgument("transactionId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->
                val transactionId = entry.arguments
                    ?.getString("transactionId")

                val viewModel: TransactionsViewModel = hiltViewModel()

                val transaction = transactionId?.let {
                    viewModel.getTransactionById(it)
                }

                if (transaction != null) {
                    TransactionDetailScreen(
                        transaction = transaction,
                        onBack = {
                            navController.popBackStack()
                        },
                        onEdit = { selectedTransaction ->
                            navController.navigate(
                                "${Screens.EDIT_TRANSACTION}/${selectedTransaction.id}"
                            )
                        },
                        onDelete = { selectedTransaction ->
                            viewModel.deleteTransaction(selectedTransaction.id)
                            navController.popBackStack()
                        }
                    )
                } else {
                    Text(
                        text = "Không tìm thấy giao dịch."
                    )
                }
            }

            composable(Screens.STAT) {
                StatisticsScreen(
                    onTransactionClick = { transactionId ->
                        navController.navigate(
                            "$TRANSACTION_DETAIL/$transactionId"
                        )
                    }
                )
            }

            composable(Screens.BUDGET) {
                BudgetScreen()
            }

        }
    }
}
