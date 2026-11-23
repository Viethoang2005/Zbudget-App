
package com.example.zbudget.ui.budget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zbudget.data.model.Budget
import com.example.zbudget.data.model.TransactionCategories
import com.example.zbudget.ui.components.CategoryEmoji
import com.example.zbudget.ui.components.CategorySelectionContent
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onSave: (category: String, limitAmount: Long) -> Boolean,
    initialBudget: Budget? = null
) {
    val colors = MaterialTheme.colorScheme
    val isEditing = initialBudget != null

    var selectedCategory by remember(initialBudget) {
        mutableStateOf(
            initialBudget?.category
                ?: TransactionCategories.expense.first().name
        )
    }

    var amountText by remember(initialBudget) {
        mutableStateOf(initialBudget?.limitAmount?.toString() ?: "")
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var showCategorySelection by remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    BackHandler(enabled = showCategorySelection) {
        showCategorySelection = false
    }

    val amount = amountText.toLongOrNull()
    val isValid = amount != null && amount > 0L
    val hasInvalidAmount = amountText.isNotBlank() && !isValid

    val formattedAmount = amount?.let {
        NumberFormat.getNumberInstance(
            Locale.forLanguageTag("vi-VN")
        ).format(it)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        contentColor = colors.onSurface,
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        )
    ) {
        if (showCategorySelection) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        showCategorySelection = false
                    },
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = colors.onSurface
                    )
                }

                CategorySelectionContent(
                    categories = TransactionCategories.expense,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = category
                        errorMessage = null
                        showCategorySelection = false
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 12.dp,
                        bottom = 32.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            color = ZbudgetGreen,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(11.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isEditing) {
                                    "Chỉnh sửa ngân sách"
                                } else {
                                    "Tạo ngân sách mới"
                                },
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onSurface
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = if (isEditing) {
                                    "Điều chỉnh hạn mức chi tiêu"
                                } else {
                                    "Đặt giới hạn chi tiêu hàng tháng"
                                },
                                fontSize = 12.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = ZbudgetGreen,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Hãy thiết lập mức chi tiêu phù hợp với kế hoạch tài chính của bạn.",
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = "DANH MỤC",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isEditing) {
                                showCategorySelection = true
                            }
                    ) {
                        OutlinedTextField(
                            value = "${CategoryEmoji(selectedCategory)}  $selectedCategory",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = colors.outline,
                                disabledTextColor = colors.onSurface,
                                disabledLabelColor = colors.onSurface,
                                disabledContainerColor = colors.surfaceVariant
                            )
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = "HẠN MỨC CHI TIÊU",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { value ->
                            if (value.all { it.isDigit() }) {
                                amountText = value
                                errorMessage = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        placeholder = {
                            Text("Ví dụ: 2000000")
                        },
                        suffix = {
                            Text(
                                text = "VNĐ",
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onSurfaceVariant
                            )
                        },
                        isError = hasInvalidAmount,
                        supportingText = {
                            when {
                                hasInvalidAmount -> {
                                    Text(
                                        "Số tiền phải lớn hơn 0 và nằm trong giới hạn cho phép."
                                    )
                                }

                                formattedAmount != null -> {
                                    Text(
                                        text = "Tương đương $formattedAmount VNĐ",
                                        color = colors.primary
                                    )
                                }

                                else -> {
                                    Text("Nhập số tiền bằng VND")
                                }
                            }
                        },
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline,
                            focusedLabelColor = colors.primary,
                            cursorColor = colors.primary,
                            focusedTextColor = colors.onSurface,
                            unfocusedTextColor = colors.onSurface,
                            focusedContainerColor = colors.surface,
                            unfocusedContainerColor = colors.surface
                        )
                    )
                }

                if (errorMessage != null) {
                    Surface(
                        color = colors.errorContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = errorMessage.orEmpty(),
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            color = colors.error
                        )
                    }
                }

                Button(
                    onClick = {
                        if (amount != null && amount > 0L) {
                            val saved = onSave(
                                selectedCategory,
                                amount
                            )

                            if (saved) {
                                onDismiss()
                            } else {
                                errorMessage =
                                    "Danh mục này đã có ngân sách trong tháng!"
                            }
                        }
                    },
                    enabled = isValid,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZbudgetGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEditing) {
                            "Lưu thay đổi"
                        } else {
                            "Tạo ngân sách"
                        },
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Hủy bỏ",
                        color = colors.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddBudgetDialogPreview() {
    ZbudgetTheme {
        AddBudgetDialog(
            onDismiss = {},
            onSave = { _, _ -> true }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditBudgetDialogPreview() {
    ZbudgetTheme {
        AddBudgetDialog(
            onDismiss = {},
            onSave = { _, _ -> true },
            initialBudget = Budget(
                id = "1",
                category = "Ăn uống",
                limitAmount = 2000000L,
                month = "2026-11"
            )
        )
    }
}
