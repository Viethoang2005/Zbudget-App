package com.example.zbudget.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.example.zbudget.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val isLoading: Boolean = true
) {
    val difference: Long
        get() = totalIncome - totalExpense
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter
        .ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT)

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            repository.transactions.collect { transactions ->
                val income = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val expense = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                val recentTransactions = transactions
                    .sortedByDescending { transaction ->
                        getTransactionDateTime(transaction)
                    }
                    .take(5)

                _uiState.value = HomeUiState(
                    transactions = recentTransactions,
                    totalIncome = income,
                    totalExpense = expense,
                    isLoading = false
                )
            }
        }
    }

    private fun getTransactionDateTime(
        transaction: Transaction
    ): LocalDateTime {
        val date = runCatching {
                LocalDate.parse(transaction.dateLabel, dateFormatter)
            }.getOrElse{
                LocalDate.MIN
            }

        val time = runCatching {
            LocalTime.parse(
                transaction.timeLabel,
                timeFormatter
            )
        }.getOrElse {
            LocalTime.MIN
        }

        return LocalDateTime.of(date, time)
    }
}