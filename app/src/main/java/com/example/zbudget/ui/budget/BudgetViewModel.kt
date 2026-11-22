
package com.example.zbudget.ui.budget

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zbudget.data.model.Budget
import com.example.zbudget.data.model.Notification
import com.example.zbudget.data.model.NotificationType
import com.example.zbudget.data.model.TransactionType
import com.example.zbudget.data.repository.BudgetRepository
import com.example.zbudget.data.repository.NotificationRepository
import com.example.zbudget.data.repository.TransactionRepository
import com.example.zbudget.utils.BudgetNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val transactionRepository: TransactionRepository,
    private val notificationRepository: NotificationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val budgetNotificationHelper = BudgetNotificationHelper(context)
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
                checkBudgetWarnings()
            }
        }

        viewModelScope.launch {
            transactionRepository.transactions.collect { transactions ->
                currentTransactions = transactions
                calculateBudget()
                checkBudgetWarnings()
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


    private fun checkBudgetWarnings() {
        val currentMonth = YearMonth.now()
        val monthString = currentMonth.toString()

        val monthlyBudgets = currentBudgets.filter {
            it.month == monthString && it.limitAmount > 0
        }

        val monthlyExpenses = currentTransactions.filter { transaction ->
            transaction.type == TransactionType.EXPENSE &&
                    parseTransactionDate(transaction.dateLabel)
                        ?.let { YearMonth.from(it) == currentMonth } == true
        }

        monthlyBudgets.forEach { budget ->
            val spentAmount = monthlyExpenses
                .filter { it.category == budget.category }
                .sumOf { it.amount }

            val percentage = spentAmount.toDouble() / budget.limitAmount

            when {
                percentage > 1.0 -> {
                    createBudgetWarning(
                        budget = budget,
                        threshold = 101,
                        spentAmount = spentAmount
                    )
                }

                percentage == 1.0 -> {
                    createBudgetWarning(
                        budget = budget,
                        threshold = 100,
                        spentAmount = spentAmount
                    )
                }

                percentage >= 0.8 -> {
                    createBudgetWarning(
                        budget = budget,
                        threshold = 80,
                        spentAmount = spentAmount
                    )
                }
            }
        }
    }



    private fun createBudgetWarning(
        budget: Budget,
        threshold: Int,
        spentAmount: Long
    ) {
        val notificationId = "budget_${budget.id}_${budget.month}_$threshold"

        val title = when (threshold) {
            101 -> "Ngân sách đã vượt giới hạn"
            100 -> "Đã sử dụng hết ngân sách"
            else -> "Sắp hết ngân sách"
        }

        val message = when (threshold) {
            101 -> "Bạn đã chi ${formatAmount(spentAmount)}, vượt quá ngân sách ${budget.category}."
            100 -> "Bạn đã sử dụng hết ngân sách ${budget.category} với số tiền ${formatAmount(spentAmount)}."
            else -> "Bạn đã sử dụng ít nhất 80% ngân sách ${budget.category}."
        }

        viewModelScope.launch {
            runCatching {
                notificationRepository.addNotificationIfNotExists(
                    notificationId = notificationId,
                    notification = Notification(
                        id = notificationId,
                        title = title,
                        message = message,
                        type = NotificationType.BUDGET_WARNING.name,
                        relatedId = budget.id
                    )
                )
            }.onSuccess { isCreated ->
                if (isCreated) {
                    budgetNotificationHelper.showNotification(
                        notificationId = notificationId,
                        title = title,
                        message = message
                    )
                }
            }
        }
    }


    private fun formatAmount(amount: Long): String {
        return "%,dđ".format(amount).replace(",", ".")
    }

    private fun parseTransactionDate(dateLabel: String): LocalDate? {
        return runCatching {
            LocalDate.parse(dateLabel, dateFormatter)
        }.getOrNull()
    }

    fun goToPreviousMonth() {
        selectMonth(_uiState.value.selectedMonth.minusMonths(1))
    }

    fun goToNextMonth() {
        val nextMonth = _uiState.value.selectedMonth.plusMonths(1)

        if (nextMonth <= YearMonth.now()) {
            selectMonth(nextMonth)
        }
    }

    fun selectMonth(month: YearMonth) {
        if (month > YearMonth.now()) return

        _uiState.value = _uiState.value.copy(
            selectedMonth = month
        )

        calculateBudget()
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
