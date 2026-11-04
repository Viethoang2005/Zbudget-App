package com.example.zbudget.ui.budget

import com.example.zbudget.data.model.Budget
import java.time.YearMonth

data class BudgetCategoryUiState(
    val budget: Budget,
    val spentAmount: Long = 0L
) {
    val remainingAmount: Long
        get() = budget.limitAmount - spentAmount

    val progress: Float
        get() = if (budget.limitAmount > 0L) {
            (spentAmount.toFloat() / budget.limitAmount).coerceIn(0f, 1f)
        } else {
            0f
        }

    val isNearLimit: Boolean
        get() = spentAmount >= budget.limitAmount * 0.8

    val isOverBudget: Boolean
        get() = spentAmount > budget.limitAmount
}

data class BudgetUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val totalBudget: Long = 0L,
    val totalSpent: Long = 0L,
    val categories: List<BudgetCategoryUiState> = emptyList(),
    val isLoading: Boolean = false
) {
    val remainingAmount: Long
        get() = totalBudget - totalSpent
}