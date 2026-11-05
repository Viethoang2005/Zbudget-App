package com.example.zbudget.ui.budget

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.zbudget.ui.components.CategoryEmoji
import com.example.zbudget.ui.components.MonthSelector
import com.example.zbudget.ui.components.formatVnd
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetText
import java.time.YearMonth
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
    var editingBudget by remember {
        mutableStateOf<Budget?>(null)
    }
    var budgetToDelete by remember { mutableStateOf<Budget?>(null) }
    BudgetScreenContent(
        uiState = uiState,
        onPreviousMonth = viewModel::goToPreviousMonth,
        onNextMonth = viewModel::goToNextMonth,
        onAddBudgetClick = {
            editingBudget = null
            showAddDialog = true
        },
        onEditBudgetClick = { budget ->
            editingBudget = budget
        },
        onDeleteBudgetClick = { budget ->
            budgetToDelete = budget
        },
        onMonthSelected = viewModel::selectMonth
    )

    if (showAddDialog || editingBudget != null) {
        AddBudgetDialog(
            onDismiss = {
                showAddDialog = false
                editingBudget = null
            },
            initialBudget = editingBudget,
            onSave = { category, limitAmount ->
                val budget = editingBudget

                if (budget != null) {
                    viewModel.updateBudget(
                        budget.copy(
                            limitAmount = limitAmount
                        )
                    )
                    true
                } else {
                    viewModel.addBudget(
                        Budget(
                            id = UUID.randomUUID().toString(),
                            category = category,
                            limitAmount = limitAmount,
                            month = uiState.selectedMonth.toString()
                        )
                    )
                }
            }
        )
    }
    budgetToDelete?.let { budget ->
        DeleteBudgetDialog(
            category = budget.category,
            onDismiss = {
                budgetToDelete = null
            },
            onConfirm = {
                viewModel.deleteBudget(budget.id)
                budgetToDelete = null
            }
        )
    }
}

@Composable
private fun DeleteBudgetDialog(
    category: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        tonalElevation = 6.dp,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = Color(0xFFFFEEEE),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = BudgetRed,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        title = {
            Text(
                text = "Xóa ngân sách?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ZbudgetText
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Bạn có chắc chắn muốn xóa ngân sách của danh mục này không?",
                    fontSize = 14.sp,
                    color = BudgetMuted,
                    lineHeight = 21.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF8F9F6),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFFFEEEE),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = CategoryEmoji(category),
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = category,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZbudgetText
                        )

                        Text(
                            text = "Ngân sách sẽ bị xóa khỏi danh sách",
                            fontSize = 11.sp,
                            color = BudgetMuted
                        )
                    }
                }

                Text(
                    text = "Lưu ý: Thao tác này không thể hoàn tác.",
                    fontSize = 12.sp,
                    color = BudgetRed,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = BudgetRed
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Xóa ngân sách",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Hủy",
                    color = BudgetMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun BudgetScreenContent(
    uiState: BudgetUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddBudgetClick: () -> Unit,
    onEditBudgetClick: (Budget) -> Unit,
    onDeleteBudgetClick: (Budget) -> Unit,
    onMonthSelected: (YearMonth) -> Unit
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
            MonthSelector(
                month = uiState.selectedMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onMonthSelected = onMonthSelected
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
                val budget = uiState.categories[index].budget
                BudgetCategoryCard(
                    categoryState = uiState.categories[index],
                    onClick = {
                        onEditBudgetClick(budget)
                    },
                    onDeleteClick = {
                        onDeleteBudgetClick(budget)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
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
    categoryState: BudgetCategoryUiState,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val budget = categoryState.budget

    val progressColor = when {
        categoryState.isOverBudget -> BudgetRed
        categoryState.isNearLimit -> BudgetOrange
        else -> ZbudgetGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
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
                        text = CategoryEmoji(budget.category),
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(categoryState.progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )

                    IconButton(
                        onClick = onDeleteClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa ngân sách",
                            tint = BudgetRed
                        )
                    }
                }
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
        onAddBudgetClick = {},
        onEditBudgetClick = {_ -> },
        onDeleteBudgetClick = {_ -> },
        onMonthSelected = {}
    )
}