package com.example.zbudget.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetText
import java.text.NumberFormat
import java.util.Locale

private val budgetCategories = listOf(
    "Ăn uống",
    "Di chuyển",
    "Học tập",
    "Giải trí",
    "Gia đình"
)

private val DialogMuted = Color(0xFF849089)
private val DialogBackground = Color(0xFFF7F9F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onSave: (category: String, limitAmount: Long) -> Boolean,
    initialBudget: Budget? = null
) {
    val isEditing = initialBudget != null

    var selectedCategory by remember(initialBudget) {
        mutableStateOf(initialBudget?.category ?: budgetCategories.first())
    }

    var amountText by remember(initialBudget) {
        mutableStateOf(initialBudget?.limitAmount?.toString() ?: "")
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val amount = amountText.toLongOrNull()
    val isValid = amount != null && amount > 0L
    val hasInvalidAmount = amountText.isNotBlank() && !isValid

    val formattedAmount = amount?.let {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(it)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        tonalElevation = 6.dp,
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = ZbudgetGreenLight,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = ZbudgetGreen,
                            modifier = Modifier.size(25.dp)
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
                            color = ZbudgetText
                        )

                        Spacer(modifier = Modifier.size(3.dp))

                        Text(
                            text = if (isEditing) {
                                "Điều chỉnh hạn mức chi tiêu"
                            } else {
                                "Đặt giới hạn chi tiêu hàng tháng"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = DialogMuted
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DialogBackground,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Hãy thiết lập mức chi tiêu phù hợp với kế hoạch tài chính của bạn.",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        color = DialogMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = "DANH MỤC",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DialogMuted
                    )

                    if (!isEditing) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {
                            OutlinedTextField(
                                value = "${categoryEmoji(selectedCategory)}  $selectedCategory",
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                }
                            ) {
                                budgetCategories.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${categoryEmoji(category)}  $category"
                                            )
                                        },
                                        onClick = {
                                            selectedCategory = category
                                            expanded = false
                                            errorMessage = null
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = "${categoryEmoji(selectedCategory)}  $selectedCategory",
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
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
                        color = DialogMuted
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
                                color = DialogMuted
                            )
                        },
                        isError = hasInvalidAmount,
                        supportingText = {
                            when {
                                hasInvalidAmount -> {
                                    Text(
                                        text = "Số tiền phải lớn hơn 0 và nằm trong giới hạn cho phép."
                                    )
                                }

                                formattedAmount != null -> {
                                    Text(
                                        text = "Tương đương $formattedAmount VNĐ",
                                        color = ZbudgetGreen
                                    )
                                }

                                else -> {
                                    Text("Nhập số tiền bằng VND")
                                }
                            }
                        }
                    )
                }

                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = errorMessage.orEmpty(),
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
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
                    containerColor = ZbudgetGreen
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEditing) "Lưu thay đổi" else "Tạo ngân sách",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Hủy bỏ",
                    color = DialogMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

private fun categoryEmoji(category: String): String {
    return when (category) {
        "Ăn uống" -> "🍜"
        "Di chuyển" -> "🚗"
        "Học tập" -> "📚"
        "Giải trí" -> "🎮"
        "Gia đình" -> "🏠"
        else -> "📦"
    }
}

@Preview(showBackground = true)
@Composable
private fun AddBudgetDialogPreview() {
    AddBudgetDialog(
        onDismiss = {},
        onSave = { _, _ -> true }
    )
}

@Preview(showBackground = true)
@Composable
private fun EditBudgetDialogPreview() {
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