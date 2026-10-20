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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.example.zbudget.ui.theme.ZbudgetCream
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenDark
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetRed
import com.example.zbudget.ui.theme.ZbudgetSecondary
import com.example.zbudget.ui.theme.ZbudgetText
import java.text.NumberFormat
import java.util.Locale


private val BottomTabs = listOf(
    "Tổng quan",
    "Giao dịch",
    "Thống kê",
    "Ngân sách"
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = ZbudgetCream,
    ) { innerPadding ->
        if (selectedTab == 0) {
            DashboardContent(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}



@Composable
private fun DashboardContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier
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
                Column {
                    Text(
                        text = "Xin chào 👋",
                        fontSize = 15.sp,
                        color = ZbudgetSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Quản lý tài chính",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZbudgetText
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(ZbudgetGreenLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "S", color = ZbudgetGreenDark,
                        fontSize = 22.sp, fontWeight = FontWeight.Bold)
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
                Text(
                    text = "Xem tất cả",
                    fontSize = 13.sp,
                    color = ZbudgetGreen,
                    fontWeight = FontWeight.SemiBold
                )
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
                items = uiState.transactions,
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
private fun BalanceCard(amount: Long) {
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
                text = "Tháng 10, 2026",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
        }
    }
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

@Composable
private fun TransactionRow(transaction: Transaction) {
    val isIncome = transaction.type == TransactionType.INCOME
    val iconBackground = if (isIncome) ZbudgetGreenLight else Color(0xFFFFEEEE)
    val amountColor = if (isIncome) ZbudgetGreen else ZbudgetRed
    val amountPrefix = if (isIncome) "+" else "−"
    val symbol = when (transaction.category) {
        "Ăn uống" -> "🍜"
        "Di chuyển" -> "🛵"
        "Học tập" -> "📚"
        "Thu nhập" -> "💼"
        "Gia đình" -> "🏠"
        else -> "💳"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = symbol, fontSize = 21.sp)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = transaction.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = ZbudgetText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${transaction.category} • ${transaction.dateLabel}",
                fontSize = 12.sp,
                color = ZbudgetSecondary
            )
        }

        Text(
            text = "$amountPrefix${formatVnd(transaction.amount)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}



private fun formatVnd(amount: Long): String {
    val locale = Locale.Builder()
        .setLanguage("vi")
        .setRegion("VN")
        .build()

    val formatter = NumberFormat.getInstance(locale)
    return "${formatter.format(amount)} đ"
}
@Composable
private fun ZbudgetTabButton(
    selected: Boolean,
    title: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.TextButton(
        onClick = onClick
    ) {
        Text(
            text = title,
            color = if (selected) ZbudgetGreenDark else ZbudgetSecondary,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DashboardContent(
        uiState = HomeUiState()
    )
}