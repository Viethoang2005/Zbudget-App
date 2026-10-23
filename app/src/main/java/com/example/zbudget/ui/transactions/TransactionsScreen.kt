
package com.example.zbudget.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.ui.components.TransactionRow
import com.example.zbudget.ui.theme.ZbudgetCream

@Composable
fun TransactionsScreen(
    onTransactionClick: (String) -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val transactions = viewModel.getTransactions()
    TransactionsScreenContent(
        transactions = transactions,
        onTransactionClick = { id ->
            onTransactionClick(id)
        }
    )
}

@Composable
fun TransactionsScreenContent(
    transactions: List<Transaction>,
    onTransactionClick: (String) -> Unit
) {
    Scaffold(
        containerColor = ZbudgetCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Giao dịch",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            if (transactions.isEmpty()) {
                Text("Chưa có giao dịch nào.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = transactions,
                        key = { it.id }
                    ) { transaction ->
                        TransactionRow(
                            transaction = transaction,
                            onClick = {
                                onTransactionClick(transaction.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TransactionScreenPreview() {
    TransactionsScreenContent(
        transactions = emptyList(),
        onTransactionClick = {}
    )
}