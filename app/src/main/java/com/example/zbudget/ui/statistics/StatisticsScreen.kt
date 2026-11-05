
package com.example.zbudget.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.example.zbudget.ui.components.CategoryEmoji
import com.example.zbudget.ui.components.MonthSelector
import com.example.zbudget.ui.components.formatVnd
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetText
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val IncomeColor = Color(0xFF219653)
private val ExpenseColor = Color(0xFFEB5757)
private val StatisticsBackground = Color(0xFFF8F9F6)
private val StatisticsMuted = Color(0xFF8A928C)

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onTransactionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    StatisticsScreenContent(
        uiState = uiState,
        onPreviousMonth = viewModel::goToPreviousMonth,
        onNextMonth = viewModel::goToNextMonth,
        onMonthSelected = viewModel::selectMonth,
        onTransactionClick = onTransactionClick
    )
}

@Composable
private fun StatisticsScreenContent(
    uiState: StatisticsUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthSelected: (YearMonth) -> Unit,
    onTransactionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(StatisticsBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 20.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Thống kê",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZbudgetText
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Theo dõi tình hình tài chính của bạn",
                    fontSize = 14.sp,
                    color = StatisticsMuted
                )
            }
        }

        item {
            MonthSelector(
                month = uiState.selectedMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onMonthSelected = onMonthSelected
            )
        }

        item {
            SummarySection(uiState = uiState)
        }

        item {
            MonthlyChartCard(
                statistics = uiState.monthlyStatistics,
                selectedMonth = uiState.selectedMonth
            )
        }

        item {
            CategoryStatisticsCard(
                statistics = uiState.categoryStatistics
            )
        }

        item {
            LargestExpensesCard(
                transactions = uiState.largestExpenses,
                onTransactionClick = onTransactionClick
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}



@Composable
private fun SummarySection(
    uiState: StatisticsUiState
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Tổng quan tháng",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ZbudgetText
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZbudgetGreen)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Số dư",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatVnd(uiState.balance),
                    color = Color.White,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${uiState.transactionCount} giao dịch trong tháng",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryItem(
                modifier = Modifier.weight(1f),
                title = "Tổng thu",
                amount = uiState.totalIncome,
                color = IncomeColor,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = IncomeColor
                    )
                }
            )

            SummaryItem(
                modifier = Modifier.weight(1f),
                title = "Tổng chi",
                amount = uiState.totalExpense,
                color = ExpenseColor,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = ExpenseColor
                    )
                }
            )
        }
    }
}

@Composable
private fun SummaryItem(
    modifier: Modifier = Modifier,
    title: String,
    amount: Long,
    color: Color,
    icon: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = StatisticsMuted,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = formatVnd(amount),
                color = color,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MonthlyChartCard(
    statistics: List<MonthlyStatistic>,
    selectedMonth: YearMonth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Thu và chi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ZbudgetText
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "So sánh trong 6 tháng gần nhất",
                fontSize = 13.sp,
                color = StatisticsMuted
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ChartLegend(color = IncomeColor, label = "Thu nhập")
                ChartLegend(color = ExpenseColor, label = "Chi tiêu")
            }

            Spacer(modifier = Modifier.height(20.dp))

            val maxAmount = (statistics.maxOfOrNull {
                maxOf(it.income, it.expense)
            } ?: 0L).coerceAtLeast(1L)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                statistics.forEach { item ->
                    val isSelected = item.month == selectedMonth

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(125.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            ChartBar(
                                amount = item.income,
                                maxAmount = maxAmount,
                                color = IncomeColor
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            ChartBar(
                                amount = item.expense,
                                maxAmount = maxAmount,
                                color = ExpenseColor
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.month.format(
                                DateTimeFormatter.ofPattern("MM")
                            ),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            color = if (isSelected) {
                                ZbudgetGreen
                            } else {
                                StatisticsMuted
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartBar(
    amount: Long,
    maxAmount: Long,
    color: Color
) {
    val barHeight = if (amount <= 0L) {
        3.dp
    } else {
        (amount.toFloat() / maxAmount.toFloat() * 115f)
            .coerceAtLeast(4f)
            .dp
    }

    Box(
        modifier = Modifier
            .width(9.dp)
            .height(barHeight)
            .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
            .background(color)
    )
}

@Composable
private fun ChartLegend(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = StatisticsMuted
        )
    }
}

@Composable
private fun CategoryStatisticsCard(
    statistics: List<CategoryStatistic>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Chi tiêu theo danh mục",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ZbudgetText
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (statistics.isEmpty()) {
                EmptyStatisticsText()
            } else {
                statistics.forEachIndexed { index, item ->
                    CategoryStatisticRow(item)

                    if (index != statistics.lastIndex) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryStatisticRow(
    statistic: CategoryStatistic
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = statistic.category,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = ZbudgetText
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatVnd(statistic.amount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZbudgetText
                )

                Text(
                    text = "${statistic.percentage.toInt()}%",
                    fontSize = 12.sp,
                    color = StatisticsMuted
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(ZbudgetGreenLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        (statistic.percentage / 100f).coerceIn(0f, 1f)
                    )
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(ZbudgetGreen)
            )
        }
    }
}

@Composable
private fun LargestExpensesCard(
    transactions: List<Transaction>,
    onTransactionClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Khoản chi lớn nhất",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ZbudgetText
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (transactions.isEmpty()) {
                EmptyStatisticsText()
            } else {
                transactions.forEachIndexed { index, transaction ->
                    LargestExpenseRow(transaction = transaction,
                        onClick = {
                            onTransactionClick(transaction.id)
                        })

                    if (index != transactions.lastIndex) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LargestExpenseRow(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(ZbudgetGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = CategoryEmoji(transaction.category),
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZbudgetText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${transaction.category} • ${transaction.dateLabel}",
                fontSize = 12.sp,
                color = StatisticsMuted
            )
        }

        Text(
            text = "-${formatVnd(transaction.amount)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ExpenseColor
        )
    }
}

@Composable
private fun EmptyStatisticsText() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Chưa có khoản chi nào trong tháng này.",
            fontSize = 13.sp,
            color = StatisticsMuted
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsScreenPreview() {
    val currentMonth = YearMonth.now()

    StatisticsScreenContent(
        uiState = StatisticsUiState(
            selectedMonth = currentMonth,
            totalIncome = 5000000L,
            totalExpense = 1800000L,
            balance = 3200000L,
            transactionCount = 8,
            monthlyStatistics = listOf(
                MonthlyStatistic(currentMonth.minusMonths(5), 3000000L, 1500000L),
                MonthlyStatistic(currentMonth.minusMonths(4), 4500000L, 2000000L),
                MonthlyStatistic(currentMonth.minusMonths(3), 3500000L, 1800000L),
                MonthlyStatistic(currentMonth.minusMonths(2), 5000000L, 2500000L),
                MonthlyStatistic(currentMonth.minusMonths(1), 4000000L, 2200000L),
                MonthlyStatistic(currentMonth, 5000000L, 1800000L)
            ),
            categoryStatistics = listOf(
                CategoryStatistic("Ăn uống", 800000L, 44.4f),
                CategoryStatistic("Di chuyển", 500000L, 27.8f),
                CategoryStatistic("Học tập", 300000L, 16.7f),
                CategoryStatistic("Giải trí", 200000L, 11.1f)
            ),
            largestExpenses = listOf(
                Transaction(
                    id = "1",
                    title = "Mua sách lập trình",
                    amount = 500000L,
                    type = TransactionType.EXPENSE,
                    category = "Học tập",
                    dateLabel = "03/11/2026",
                    timeLabel = "10:00"
                ),
                Transaction(
                    id = "2",
                    title = "Ăn tối",
                    amount = 250000L,
                    type = TransactionType.EXPENSE,
                    category = "Ăn uống",
                    dateLabel = "02/11/2026",
                    timeLabel = "19:00"
                )
            )
        ),
        onPreviousMonth = {},
        onNextMonth = {},
        onTransactionClick = {_ -> },
        onMonthSelected = {}
    )
}
