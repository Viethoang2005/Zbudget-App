package com.example.zbudget.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zbudget.data.model.Budget
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.example.zbudget.data.repository.BudgetRepository
import com.example.zbudget.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState = _uiState.asStateFlow()

    private var currentBudgets = budgetRepository.budgets.value
    private var currentTransactions = transactionRepository.transactions.value

    private val dateFormatter = DateTimeFormatter
        .ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT)

    init {
        calculateBudget()

        viewModelScope.launch {
            budgetRepository.budgets.collect { budgets ->
                currentBudgets = budgets
                calculateBudget()
            }
        }

        viewModelScope.launch {
            transactionRepository.transactions.collect { transactions ->
                currentTransactions = transactions
                calculateBudget()
            }
        }
    }

    private fun calculateBudget() {
        val selectedMonth = _uiState.value.selectedMonth
        val monthString = selectedMonth.toString()

        val monthlyBudgets = currentBudgets.filter {
            it.month == monthString
        }

        val monthlyExpenses = currentTransactions.filter { transaction ->
            transaction.type == TransactionType.EXPENSE &&
                    parseTransactionDate(transaction.dateLabel)
                        ?.let { YearMonth.from(it) == selectedMonth } == true
        }

        val categories = monthlyBudgets.map { budget ->
            val spentAmount = monthlyExpenses
                .filter { it.category == budget.category }
                .sumOf { it.amount }

            BudgetCategoryUiState(
                budget = budget,
                spentAmount = spentAmount
            )
        }

        val totalBudget = monthlyBudgets.sumOf {
            it.limitAmount
        }

        val totalSpent = monthlyExpenses.sumOf {
            it.amount
        }

        _uiState.value = _uiState.value.copy(
            totalBudget = totalBudget,
            totalSpent = totalSpent,
            categories = categories
        )
    }

    private fun parseTransactionDate(dateLabel: String): LocalDate? {
        return when (dateLabel) {
            "Hôm nay" -> LocalDate.now()
            "Hôm qua" -> LocalDate.now().minusDays(1)
            else -> runCatching {
                LocalDate.parse(dateLabel, dateFormatter)
            }.getOrNull()
        }
    }

    fun goToPreviousMonth() {
        val currentMonth = _uiState.value.selectedMonth

        _uiState.value = _uiState.value.copy(
            selectedMonth = currentMonth.minusMonths(1)
        )

        calculateBudget()
    }

    fun goToNextMonth() {
        val currentMonth = _uiState.value.selectedMonth

        if (currentMonth < YearMonth.now()) {
            _uiState.value = _uiState.value.copy(
                selectedMonth = currentMonth.plusMonths(1)
            )

            calculateBudget()
        }
    }

    fun addBudget(budget: Budget): Boolean {
        val alreadyExists = budgetRepository.budgets.value.any {
            it.category == budget.category &&
                    it.month == budget.month
        }

        if (alreadyExists) {
            return false
        }

        budgetRepository.addBudget(budget)
        return true
    }

    fun updateBudget(budget: Budget) {
        budgetRepository.updateBudget(budget)
    }

    fun deleteBudget(id: String) {
        budgetRepository.deleteBudget(id)
    }
}