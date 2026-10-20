package com.example.zbudget.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zbudget.ui.components.TransactionRow
import com.example.zbudget.ui.components.formatVnd
import com.example.zbudget.ui.theme.ZbudgetCream
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenDark
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetRed
import com.example.zbudget.ui.theme.ZbudgetSecondary
import com.example.zbudget.ui.theme.ZbudgetText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToTransactions: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = ZbudgetCream,
    ) { innerPadding ->
        if (selectedTab == 0) {
            DashboardContent(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                onNavigateToTransactions = onNavigateToTransactions
            )
        }
    }
}


@Composable
private fun DashboardContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onNavigateToTransactions: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dashboard",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZbudgetText
                )

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(ZbudgetGreenLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S", color = ZbudgetGreenDark,
                        fontSize = 22.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            BalanceCard(
                amount = uiState.difference
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Tổng thu",
                    amount = uiState.totalIncome,
                    symbol = "↙",
                    amountColor = ZbudgetGreen,
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "Tổng chi",
                    amount = uiState.totalExpense,
                    symbol = "↗",
                    amountColor = ZbudgetRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Giao dịch gần đây",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZbudgetText
                )
                TextButton(
                    onClick = onNavigateToTransactions
                ) {
                    Text(
                        text = "Xem tất cả ->",
                        fontSize = 13.sp,
                        color = ZbudgetGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (uiState.transactions.isEmpty()) {
            item {
                Text(
                    text = "Chưa có giao dịch nào.",
                    color = ZbudgetSecondary,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            items(
                items = uiState.transactions.take(5),
                key = { it.id }
            ) { transaction ->
                TransactionRow(transaction = transaction)
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun BalanceCard(amount: Long, month: String = currentMonthLabel()) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ZbudgetGreenDark
        )
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = "Thu - chi trong danh sách mẫu",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formatVnd(amount),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = month,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
        }
    }
}

private fun currentMonthLabel(): String {
    val locale = Locale.Builder()
        .setLanguage("vi")
        .setRegion("VN")
        .build()
    return SimpleDateFormat(
        "'Tháng' M, yyyy",
        locale
    ).format(Date())
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Long,
    symbol: String,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(ZbudgetGreenLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol,
                        color = ZbudgetGreenDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = title,
                    color = ZbudgetSecondary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formatVnd(amount),
                color = amountColor,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DashboardContent(
        uiState = HomeUiState(),
        onNavigateToTransactions = {}
    )
}