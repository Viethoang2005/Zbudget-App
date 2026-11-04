
package com.example.zbudget.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val budgetCategories = listOf(
    "Ăn uống",
    "Di chuyển",
    "Học tập",
    "Giải trí",
    "Gia đình"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onSave: (category: String, limitAmount: Long) -> Boolean
) {
    var selectedCategory by remember {
        mutableStateOf(budgetCategories.first())
    }

    var amountText by remember {
        mutableStateOf("")
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    val amount = amountText.toLongOrNull()
    val isValid = amount != null && amount > 0
    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Thêm ngân sách")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Danh mục")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
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
                                    Text(category)
                                },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) {
                            amountText = value
                        }
                    },
                    label = {
                        Text("Hạn mức ngân sách")
                    },
                    placeholder = {
                        Text("Ví dụ: 2000000")
                    },
                    supportingText = {
                        Text("Nhập số tiền bằng VND")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        val saved = onSave(
                            selectedCategory,
                            amount!!
                        )

                        if (saved) {
                            onDismiss()
                        } else {
                            errorMessage =
                                "Danh mục này đã có ngân sách trong tháng!"
                        }
                    }
                },
                enabled = isValid
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Hủy")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AddBudgetDialogPreview() {
    AddBudgetDialog(
        onDismiss = {},
        onSave = { _, _ -> true}
    )
}
