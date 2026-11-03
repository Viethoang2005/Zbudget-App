package com.example.zbudget.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionCategories
import com.example.zbudget.ui.components.CategoryOption
import com.example.zbudget.ui.components.CategorySelectionContent
import com.example.zbudget.ui.components.TransactionRow
import com.example.zbudget.ui.theme.ZbudgetCream
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenDark
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun TransactionsScreen(
    onTransactionClick: (String) -> Unit,
    onAddClick: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val transactions = viewModel.getFilteredTransactions()

    TransactionsScreenContent(
        state = state,
        transactions = transactions,
        onTabSelected = viewModel::onTabSelected,
        onSortSelected = viewModel::onSortSelected,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onFilterDraftChanged = viewModel::onFilterDraftChanged,
        onApplyFilter = viewModel::applyFilter,
        onResetDraftFilter = viewModel::resetDraftFilter,
        onClearFilters = viewModel::clearFilters,
        onTransactionClick = onTransactionClick,
        onAddClick = onAddClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreenContent(
    state: TransactionsUiState,
    transactions: List<Transaction>,
    onTabSelected: (TransactionTab) -> Unit,
    onSortSelected: (TransactionSort) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onFilterDraftChanged: (TransactionFilter) -> Unit,
    onApplyFilter: () -> Unit,
    onResetDraftFilter: () -> Unit,
    onClearFilters: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showDateMenu by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val selectedTab = state.selectedTab
    val selectedSort = state.selectedSort
    val appliedFilter = state.appliedFilter
    val draftFilter = state.draftFilter
    val searchQuery = state.searchQuery
    val categories = state.categories

    val activeFilters = buildList {
        if (appliedFilter.dateFilter != TransactionDateFilter.ALL) {
            add(appliedFilter.dateFilter.label)
        }

        if (appliedFilter.category != "Tất cả danh mục") {
            add(appliedFilter.category)
        }

        if (appliedFilter.minAmount.isNotBlank()) {
            add("Từ ${appliedFilter.minAmount}đ")
        }

        if (appliedFilter.maxAmount.isNotBlank()) {
            add("Đến ${appliedFilter.maxAmount}đ")
        }
    }
    val categoryOptions = remember(categories) {
        val allCategories = (
                TransactionCategories.expense +
                        TransactionCategories.income
                ).distinctBy { it.name }

        val uniqueCategories = categories.distinct()

        listOf(
            CategoryOption("Tất cả danh mục", "📋")
        ) + uniqueCategories
            .filterNot { it == "Tất cả danh mục" }
            .map { categoryName ->
                allCategories.find { it.name == categoryName }
                    ?: CategoryOption(categoryName, "📦")
            }
    }

    LaunchedEffect(showSearch) {
        if (showSearch) {
            delay(100.milliseconds)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    Scaffold(
        containerColor = ZbudgetCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Giao dịch",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = ZbudgetGreenDark
                )

                IconButton(
                    onClick = {
                        showSearch = !showSearch
                        if (!showSearch) {
                            keyboardController?.hide()
                            onSearchQueryChanged("")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Tìm kiếm",
                        tint = ZbudgetGreenDark
                    )
                }
            }

            if (showSearch) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester = focusRequester),
                    singleLine = true,
                    label = {
                        Text("Tìm tên giao dịch hoặc ghi chú")
                    },
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab

                    Button(
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) {
                                ZbudgetGreen
                            } else {
                                ZbudgetGreen.copy(alpha = 0.10f)
                            },
                            contentColor = if (isSelected) {
                                Color.White
                            } else {
                                ZbudgetGreenDark
                            }
                        )
                    ) {
                        Text(
                            text = tab.label,
                            fontWeight = if (isSelected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${transactions.size} giao dịch",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ZbudgetGreenDark
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onAddClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm giao dịch"
                        )
                    }
                    IconButton(
                        onClick = {
                            onFilterDraftChanged(appliedFilter)
                            showFilterSheet = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Lọc giao dịch",
                            tint = if (appliedFilter != TransactionFilter()) {
                                ZbudgetGreen
                            } else {
                                ZbudgetGreenDark
                            }
                        )
                    }

                    Box {
                        TextButton(
                            onClick = { showSortMenu = true }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = null,
                                tint = ZbudgetGreen
                            )

                            Text(
                                text = selectedSort.label,
                                color = ZbudgetGreenDark
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = {
                                showSortMenu = false
                            }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mới nhất") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    onSortSelected(TransactionSort.NEWEST)
                                    showSortMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Cũ nhất") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    onSortSelected(TransactionSort.OLDEST)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            if (activeFilters.isNotEmpty()) {
                Text(
                    text = activeFilters.joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = ZbudgetGreenDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (transactions.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Không tìm thấy giao dịch",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ZbudgetGreenDark
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Thử thay đổi hoặc đặt lại bộ lọc.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        TextButton(
                            onClick = onClearFilters
                        ) {
                            Text("Xóa bộ lọc")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = transactions,
                        key = { it.id }
                    ) { transaction ->
                        TransactionRow(
                            transaction = transaction,
                            onClick = {
                                onTransactionClick(transaction.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showFilterSheet = false
                showDateMenu = false
                showCategoryPicker = false
            },
            containerColor = ZbudgetCream
        ) {
            if (showCategoryPicker) {
                CategorySelectionContent(
                    categories = categoryOptions,
                    selectedCategory = draftFilter.category,
                    onCategorySelected = { selectedCategory ->
                        onFilterDraftChanged(
                            draftFilter.copy(category = selectedCategory)
                        )
                        showCategoryPicker = false
                    }
                )
            } else {


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Bộ lọc giao dịch",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ZbudgetGreenDark
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Khoảng thời gian",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                showDateMenu = true
                                showCategoryPicker = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = draftFilter.dateFilter.label,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Chọn khoảng thời gian"
                            )
                        }

                        DropdownMenu(
                            expanded = showDateMenu,
                            onDismissRequest = {
                                showDateMenu = false
                            },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            TransactionDateFilter.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        onFilterDraftChanged(
                                            draftFilter.copy(dateFilter = option)
                                        )
                                        showDateMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Danh mục",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            showCategoryPicker = true
                            showDateMenu = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = draftFilter.category,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Chọn danh mục"
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Khoảng tiền (VNĐ)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = draftFilter.minAmount,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) {
                                    onFilterDraftChanged(
                                        draftFilter.copy(minAmount = value)
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Từ") },
                            placeholder = { Text("0") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = draftFilter.maxAmount,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) {
                                    onFilterDraftChanged(
                                        draftFilter.copy(maxAmount = value)
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Đến") },
                            placeholder = { Text("Không giới hạn") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    val minAmount = draftFilter.minAmount.toLongOrNull()
                    val maxAmount = draftFilter.maxAmount.toLongOrNull()

                    val invalidRange =
                        minAmount != null &&
                                maxAmount != null &&
                                minAmount > maxAmount

                    if (invalidRange) {
                        Text(
                            text = "Số tiền tối thiểu phải nhỏ hơn hoặc bằng số tiền tối đa.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = {
                                onResetDraftFilter()
                                showDateMenu = false
                                showCategoryPicker = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Đặt lại")
                        }

                        Button(
                            onClick = {
                                if (!invalidRange) {
                                    onApplyFilter()
                                    showFilterSheet = false
                                    showDateMenu = false
                                    showCategoryPicker = false
                                }
                            },
                            enabled = !invalidRange,
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ZbudgetGreen
                            )
                        ) {
                            Text("Áp dụng")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    MaterialTheme {
        TransactionsScreenContent(
            state = TransactionsUiState(),
            transactions = emptyList(),
            onTabSelected = {},
            onSortSelected = {},
            onSearchQueryChanged = {},
            onFilterDraftChanged = {},
            onApplyFilter = {},
            onResetDraftFilter = {},
            onClearFilters = {},
            onTransactionClick = {},
            onAddClick = {}
        )
    }
}

