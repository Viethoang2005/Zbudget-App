package com.example.zbudget.ui.budget

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zbudget.data.model.Budget
import com.example.zbudget.ui.components.formatVnd
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetText
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

private val BudgetBackground = Color(0xFFF8F9F6)
private val BudgetMuted = Color(0xFF8A928C)
private val BudgetRed = Color(0xFFEB5757)
private val BudgetOrange = Color(0xFFF2994A)

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember {
        mutableStateOf(false)
    }

    BudgetScreenContent(
        uiState = uiState,
        onPreviousMonth = viewModel::goToPreviousMonth,
        onNextMonth = viewModel::goToNextMonth,
        onAddBudgetClick = {
            showAddDialog = true
        }
    )

    if (showAddDialog) {
        AddBudgetDialog(
            onDismiss = {
                showAddDialog = false
            },
            onSave = { category, limitAmount ->
                viewModel.addBudget(
                    Budget(
                        id = UUID.randomUUID().toString(),
                        category = category,
                        limitAmount = limitAmount,
                        month = uiState.selectedMonth.toString()
                    )
                )
            }
        )
    }
}

@Composable
private fun BudgetScreenContent(
    uiState: BudgetUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddBudgetClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BudgetBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 20.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        item {
            Column {
                Text(
                    text = "Ngân sách",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZbudgetText
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Kiểm soát chi tiêu theo kế hoạch",
                    fontSize = 14.sp,
                    color = BudgetMuted
                )
            }
        }

        item {
            BudgetMonthSelector(
                month = uiState.selectedMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )
        }
        item {
            Button(
                onClick = onAddBudgetClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm ngân sách")
            }
        }
        item {
            BudgetOverviewCard(uiState = uiState)
        }

        item {
            Text(
                text = "Ngân sách theo danh mục",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ZbudgetText
            )
        }

        if (uiState.categories.isEmpty()) {
            item {
                EmptyBudgetContent()
            }
        } else {
            items(uiState.categories.size) { index ->
                BudgetCategoryCard(
                    categoryState = uiState.categories[index]
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BudgetMonthSelector(
    month: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern(
        "'Tháng' MM, yyyy",
        Locale.forLanguageTag("vi-VN")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Tháng trước",
                    tint = ZbudgetText
                )
            }

            Text(
                text = month.format(formatter),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZbudgetText
            )

            IconButton(
                onClick = onNextMonth,
                enabled = month < YearMonth.now()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Tháng sau",
                    tint = if (month < YearMonth.now()) {
                        ZbudgetText
                    } else {
                        BudgetMuted.copy(alpha = 0.4f)
                    }
                )
            }
        }
    }
}

@Composable
private fun BudgetOverviewCard(
    uiState: BudgetUiState
) {
    val progress = if (uiState.totalBudget > 0L) {
        (uiState.totalSpent.toFloat() / uiState.totalBudget)
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    val progressColor = when {
        uiState.totalBudget <= 0L -> ZbudgetGreen
        uiState.totalSpent > uiState.totalBudget -> BudgetRed
        progress >= 0.8f -> BudgetOrange
        else -> Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ZbudgetGreen)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Tổng ngân sách tháng",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatVnd(uiState.totalBudget),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Đã chi",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formatVnd(uiState.totalSpent),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Còn lại",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formatVnd(uiState.remainingAmount),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(CircleShape),
                color = progressColor,
                trackColor = Color.White.copy(alpha = 0.25f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (uiState.totalBudget > 0L) {
                    "${(uiState.totalSpent.toFloat() / uiState.totalBudget * 100).toInt()}% ngân sách đã sử dụng"
                } else {
                    "Chưa thiết lập ngân sách"
                },
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun BudgetCategoryCard(
    categoryState: BudgetCategoryUiState
) {
    val budget = categoryState.budget

    val progressColor = when {
        categoryState.isOverBudget -> BudgetRed
        categoryState.isNearLimit -> BudgetOrange
        else -> ZbudgetGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ZbudgetGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryEmoji(budget.category),
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = budget.category,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZbudgetText
                    )

                    Text(
                        text = "Ngân sách: ${formatVnd(budget.limitAmount)}",
                        fontSize = 12.sp,
                        color = BudgetMuted
                    )
                }

                Text(
                    text = "${(categoryState.progress * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { categoryState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = progressColor,
                trackColor = ZbudgetGreenLight
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Đã chi",
                        fontSize = 11.sp,
                        color = BudgetMuted
                    )

                    Text(
                        text = formatVnd(categoryState.spentAmount),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZbudgetText
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (categoryState.isOverBudget) {
                            "Vượt ngân sách"
                        } else {
                            "Còn lại"
                        },
                        fontSize = 11.sp,
                        color = if (categoryState.isOverBudget) {
                            BudgetRed
                        } else {
                            BudgetMuted
                        }
                    )

                    Text(
                        text = formatVnd(
                            kotlin.math.abs(categoryState.remainingAmount)
                        ),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (categoryState.isOverBudget) {
                            BudgetRed
                        } else {
                            ZbudgetText
                        }
                    )
                }
            }

            if (categoryState.isNearLimit) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (categoryState.isOverBudget) {
                        "Bạn đã vượt quá hạn mức của danh mục này."
                    } else {
                        "Bạn đã sử dụng gần hết ngân sách."
                    },
                    fontSize = 12.sp,
                    color = progressColor
                )
            }
        }
    }
}

@Composable
private fun EmptyBudgetContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊",
                fontSize = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Chưa có ngân sách",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZbudgetText
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Bạn chưa thiết lập ngân sách cho tháng này.",
                fontSize = 13.sp,
                color = BudgetMuted
            )
        }
    }
}

private fun categoryEmoji(category: String): String {
    return when (category) {
        "Ăn uống" -> "🍜"
        "Di chuyển" -> "🚗"
        "Mua sắm" -> "🛍️"
        "Học tập" -> "📚"
        "Giải trí" -> "🎮"
        "Gia đình" -> "🏠"
        "Sức khỏe" -> "💊"
        "Làm đẹp" -> "💄"
        "Du lịch" -> "✈️"
        "Hóa đơn" -> "🧾"
        else -> "📦"
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetScreenPreview() {
    val currentMonth = YearMonth.now()

    BudgetScreenContent(
        uiState = BudgetUiState(
            selectedMonth = currentMonth,
            totalBudget = 4000000L,
            totalSpent = 2800000L,
            categories = listOf(
                BudgetCategoryUiState(
                    budget = Budget(
                        id = "1",
                        category = "Ăn uống",
                        limitAmount = 2000000L,
                        month = currentMonth.toString()
                    ),
                    spentAmount = 1200000L
                ),
                BudgetCategoryUiState(
                    budget = Budget(
                        id = "2",
                        category = "Di chuyển",
                        limitAmount = 500000L,
                        month = currentMonth.toString()
                    ),
                    spentAmount = 200000L
                ),
                BudgetCategoryUiState(
                    budget = Budget(
                        id = "3",
                        category = "Học tập",
                        limitAmount = 1000000L,
                        month = currentMonth.toString()
                    ),
                    spentAmount = 850000L
                ),
                BudgetCategoryUiState(
                    budget = Budget(
                        id = "4",
                        category = "Giải trí",
                        limitAmount = 500000L,
                        month = currentMonth.toString()
                    ),
                    spentAmount = 550000L
                )
            )
        ),
        onPreviousMonth = {},
        onNextMonth = {},
        onAddBudgetClick = {}
    )
}