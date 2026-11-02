package com.example.zbudget.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.example.zbudget.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<String> = listOf("Tất cả danh mục"),
    val selectedTab: TransactionTab = TransactionTab.ALL,
    val selectedSort: TransactionSort = TransactionSort.NEWEST,
    val searchQuery: String = "",
    val appliedFilter: TransactionFilter = TransactionFilter(),
    val draftFilter: TransactionFilter = TransactionFilter()
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TransactionsUiState()
    )
    val uiState: StateFlow<TransactionsUiState> =
        _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.transactions.collect { transactions ->
                _uiState.update { currentState ->
                    currentState.copy(
                        transactions = transactions,
                        categories = listOf("Tất cả danh mục") +
                                transactions.map { it.category }.distinct()
                    )
                }
            }
        }

    }

    fun getTransactionById(id: String): Transaction? {
        return repository.getTransactionById(id)
    }

    fun onTabSelected(tab: TransactionTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onSortSelected(sort: TransactionSort) {
        _uiState.update { it.copy(selectedSort = sort) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onFilterDraftChanged(filter: TransactionFilter) {
        _uiState.update { it.copy(draftFilter = filter) }
    }

    fun applyFilter() {
        _uiState.update {
            it.copy(appliedFilter = it.draftFilter)
        }
    }

    fun resetDraftFilter() {
        _uiState.update {
            it.copy(draftFilter = TransactionFilter())
        }
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                appliedFilter = TransactionFilter(),
                draftFilter = TransactionFilter(),
                searchQuery = ""
            )
        }
    }

    fun getFilteredTransactions(): List<Transaction> {
        val state = _uiState.value
        val today = LocalDate.now()

        fun transactionDate(transaction: Transaction): LocalDate {
            return when (transaction.dateLabel) {
                "Hôm nay" -> today
                "Hôm qua" -> today.minusDays(1)
                else -> {
                    val parts = transaction.dateLabel.split("/")
                    if (parts.size == 3) {
                        runCatching {
                            LocalDate.of(
                                parts[2].toInt(),
                                parts[1].toInt(),
                                parts[0].toInt()
                            )
                        }.getOrDefault(today)
                    } else {
                        today
                    }
                }
            }
        }

        fun transactionDateTime(
            transaction: Transaction
        ): LocalDateTime {
            val timeParts = transaction.timeLabel.split(":")
            val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
            val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

            return transactionDate(transaction).atTime(
                hour.coerceIn(0, 23),
                minute.coerceIn(0, 59)
            )
        }

        val filter = state.appliedFilter

        return state.transactions
            .filter { transaction ->
                val date = transactionDate(transaction)
                val query = state.searchQuery.trim()
                val amount = transaction.amount

                val matchesTab = when (state.selectedTab) {
                    TransactionTab.ALL -> true
                    TransactionTab.INCOME ->
                        transaction.type == TransactionType.INCOME
                    TransactionTab.EXPENSE ->
                        transaction.type == TransactionType.EXPENSE
                }

                val matchesSearch = query.isBlank() ||
                        transaction.title.contains(query, ignoreCase = true) ||
                        transaction.note.contains(query, ignoreCase = true)

                val matchesCategory =
                    filter.category == "Tất cả danh mục" ||
                            transaction.category == filter.category

                val matchesMin =
                    filter.minAmount.toLongOrNull()?.let {
                        amount >= it
                    } ?: true

                val matchesMax =
                    filter.maxAmount.toLongOrNull()?.let {
                        amount <= it
                    } ?: true

                val matchesDate = when (filter.dateFilter) {
                    TransactionDateFilter.ALL -> true
                    TransactionDateFilter.LAST_7_DAYS ->
                        !date.isBefore(today.minusDays(6)) &&
                                !date.isAfter(today)
                    TransactionDateFilter.THIS_MONTH ->
                        YearMonth.from(date) == YearMonth.from(today)
                    TransactionDateFilter.LAST_MONTH ->
                        YearMonth.from(date) ==
                                YearMonth.from(today).minusMonths(1)
                }

                matchesTab &&
                        matchesSearch &&
                        matchesCategory &&
                        matchesMin &&
                        matchesMax &&
                        matchesDate
            }
            .let { filtered ->
                when (state.selectedSort) {
                    TransactionSort.NEWEST ->
                        filtered.sortedByDescending {
                            transactionDateTime(it)
                        }
                    TransactionSort.OLDEST ->
                        filtered.sortedBy {
                            transactionDateTime(it)
                        }
                }
            }
    }
    fun addTransaction(transaction: Transaction) {
        repository.addTransaction(transaction)
    }
    fun updateTransaction(transaction: Transaction) {
        repository.updateTransaction(transaction)
    }
    fun deleteTransaction(id: String) {
        repository.deleteTransaction(id)
    }
    fun generateTransactionId(): String {
        return UUID.randomUUID().toString()
    }
}
