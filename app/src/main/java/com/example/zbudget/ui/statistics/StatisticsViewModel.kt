
package com.example.zbudget.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.example.zbudget.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.ResolverStyle

data class MonthlyStatistic(
    val month: YearMonth,
    val income: Long,
    val expense: Long
)

data class CategoryStatistic(
    val category: String,
    val amount: Long,
    val percentage: Float
)

data class StatisticsUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val balance: Long = 0L,
    val transactionCount: Int = 0,
    val monthlyStatistics: List<MonthlyStatistic> = emptyList(),
    val categoryStatistics: List<CategoryStatistic> = emptyList(),
    val largestExpenses: List<Transaction> = emptyList()
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()



    private val dateFormatter = DateTimeFormatter
        .ofPattern("dd/MM/uuuu", Locale.forLanguageTag("vi-VN"))
        .withResolverStyle(ResolverStyle.STRICT)
    private fun parseTransactionDate(transaction: Transaction): LocalDate? {
        return runCatching {
            LocalDate.parse(transaction.dateLabel, dateFormatter)
        }.getOrNull()
    }


    private fun calculateStatistics(transactions: List<Transaction>) {
        val selectedMonth = _uiState.value.selectedMonth

        val datedTransactions = transactions.mapNotNull { transaction ->
            parseTransactionDate(transaction)?.let { date ->
                transaction to date
            }
        }
        val monthlyTransactions = datedTransactions.filter { (_, date) ->
            YearMonth.from(date) == selectedMonth
        }.map { (transaction, _) ->
            transaction
        }

        val income = monthlyTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expense = monthlyTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val categoryStatistics = monthlyTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .map { (category, categoryTransactions) ->
                category to categoryTransactions.sumOf { it.amount }
            }
            .sortedByDescending { it.second }
            .map { (category, amount) ->
                CategoryStatistic(
                    category = category,
                    amount = amount,
                    percentage = if (expense > 0L) {
                        amount.toFloat() / expense.toFloat() * 100f
                    } else {
                        0f
                    }
                )
            }

        val monthlyStatistics = (5 downTo 0).map { offset ->
            val month = selectedMonth.minusMonths(offset.toLong())
            val monthTransactions = datedTransactions
                .filter { (_, date) ->
                    YearMonth.from(date) == month
                }
                .map { (transaction, _) ->
                    transaction
                }

            MonthlyStatistic(
                month = month,
                income = monthTransactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount },
                expense = monthTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
            )
        }

        val largestExpenses = monthlyTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sortedByDescending { it.amount }
            .take(5)

        _uiState.update {
            it.copy(
                selectedMonth = selectedMonth,
                totalIncome = income,
                totalExpense = expense,
                balance = income - expense,
                transactionCount = monthlyTransactions.size,
                monthlyStatistics = monthlyStatistics,
                categoryStatistics = categoryStatistics,
                largestExpenses = largestExpenses
            )
        }
    }

    init {
        calculateStatistics(repository.getTransactions())
        viewModelScope.launch {
            repository.transactions.collect { transactions ->
                calculateStatistics(transactions)
            }
        }
    }
    fun selectMonth(month: YearMonth) {
        _uiState.update {
            it.copy(selectedMonth = month)
        }

        calculateStatistics(repository.getTransactions())
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
}
