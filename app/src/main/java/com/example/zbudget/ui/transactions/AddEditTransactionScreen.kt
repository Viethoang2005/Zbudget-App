
package com.example.zbudget.ui.transactions

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    transactionId: String? = null,
    onBack: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val colors = MaterialTheme.colorScheme
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Ăn uống") }
    var dateLabel by remember {
        mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
    }
    var timeLabel by remember {
        mutableStateOf(java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
    }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = remember(uiState.categories) {
        (
                uiState.categories.filter { it != "Tất cả danh mục" } +
                        listOf(
                            "Ăn uống",
                            "Di chuyển",
                            "Mua sắm",
                            "Học tập",
                            "Giải trí",
                            "Gia đình",
                            "Lương",
                            "Thưởng",
                            "Khác"
                        )
                ).distinct()
    }

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            val transaction = viewModel.getTransactionById(transactionId)
            transaction?.let {
                title = it.title
                amount = it.amount.toString()
                category = it.category
                dateLabel = it.dateLabel
                timeLabel = it.timeLabel
                note = it.note
                selectedType = it.type
            }
        }
    }

    val initialDate = remember(dateLabel) {
        runCatching {
            LocalDate.parse(
                dateLabel,
                DateTimeFormatter.ofPattern("dd/MM/uuuu")
            )
        }.getOrDefault(LocalDate.now())
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )

    val timeParts = timeLabel.split(":")
    val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 12
    val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour.coerceIn(0, 23),
        initialMinute = initialMinute.coerceIn(0, 59),
        is24Hour = true
    )

    Scaffold(
        containerColor = colors.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại"
                    )
                }

                Text(
                    text = if (transactionId == null) "Thêm giao dịch" else "Sửa giao dịch",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        val parsedAmount = amount
                            .replace(",", "")
                            .replace(".", "")
                            .toLongOrNull()

                        val validDate = runCatching {
                            LocalDate.parse(
                                dateLabel,
                                DateTimeFormatter.ofPattern("dd/MM/uuuu")
                                    .withResolverStyle(ResolverStyle.STRICT)
                            )
                        }.isSuccess

                        val validTime = runCatching {
                            java.time.LocalTime.parse(
                                timeLabel,
                                DateTimeFormatter.ofPattern("HH:mm")
                            )
                        }.isSuccess

                        errorMessage = when {
                            title.isBlank() -> "Vui lòng nhập tên giao dịch"
                            parsedAmount == null || parsedAmount <= 0L ->
                                "Vui lòng nhập số tiền hợp lệ"
                            category.isBlank() -> "Vui lòng chọn danh mục"
                            !validDate -> "Ngày không hợp lệ"
                            !validTime -> "Giờ không hợp lệ"
                            else -> null
                        }

                        if (errorMessage == null && parsedAmount != null) {
                            val transaction = Transaction(
                                id = transactionId ?: viewModel.generateTransactionId(),
                                title = title.trim(),
                                amount = parsedAmount,
                                type = selectedType,
                                category = category,
                                dateLabel = dateLabel,
                                timeLabel = timeLabel,
                                note = note.trim()
                            )

                            if (transactionId == null) {
                                viewModel.addTransaction(transaction)
                            } else {
                                viewModel.updateTransaction(transaction)
                            }

                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Text(
                        text = if (transactionId == null) "Lưu giao dịch" else "Cập nhật giao dịch",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FormLabel("Loại giao dịch")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surface)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TypeOption(
                        text = "Chi tiêu",
                        selected = selectedType == TransactionType.EXPENSE,
                        selectedColor = colors.error,
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedType = TransactionType.EXPENSE
                    }

                    TypeOption(
                        text = "Thu nhập",
                        selected = selectedType == TransactionType.INCOME,
                        selectedColor = colors.primary,
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedType = TransactionType.INCOME
                    }
                }
            }

            FormSection(title = "Thông tin giao dịch") {
                FormLabel("Tên giao dịch")

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ví dụ: Ăn trưa, mua sách...") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                FormLabel("Số tiền")

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { char -> char.isDigit() } },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập số tiền") },
                    suffix = { Text("đ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                FormLabel("Danh mục")

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary
                        )
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { categoryExpanded = true }
                    )

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(colors.surface)
                    ) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    category = item
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            FormSection(title = "Thời gian") {
                FormLabel("Ngày giao dịch")

                SelectableField(
                    value = dateLabel,
                    icon = Icons.Default.CalendarMonth
                ) {
                    showDatePicker = true
                }

                Spacer(modifier = Modifier.height(8.dp))

                FormLabel("Giờ giao dịch")

                SelectableField(
                    value = timeLabel,
                    icon = Icons.Default.AccessTime
                ) {
                    showTimePicker = true
                }
            }

            FormSection(title = "Ghi chú") {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    placeholder = { Text("Thêm ghi chú nếu cần...") },
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary
                    )
                )
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = colors.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            dateLabel = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Chọn giờ giao dịch") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        timeLabel = String.format(
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showTimePicker = false
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
private fun FormSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        content()
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun TypeOption(
    text: String,
    selected: Boolean,
    selectedColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) selectedColor.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) selectedColor
            else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SelectableField(
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = value,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
