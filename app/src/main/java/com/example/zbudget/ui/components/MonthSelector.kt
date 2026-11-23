package com.example.zbudget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetText
import com.example.zbudget.ui.theme.ZbudgetTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthSelector(
    month: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthSelected: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val formatter = DateTimeFormatter.ofPattern(
        "'Tháng' MM, yyyy",
        Locale.forLanguageTag("vi-VN")
    )

    var showBottomSheet by remember { mutableStateOf(false) }
    var draftMonth by remember(month) { mutableStateOf(month) }
    var displayedYear by remember(month) { mutableIntStateOf(month.year) }

    val currentMonth = YearMonth.now()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Tháng trước",
                    tint = colors.onSurface
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        draftMonth = month
                        displayedYear = month.year
                        showBottomSheet = true
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = month.format(formatter),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Chọn tháng",
                    tint = colors.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = onNextMonth,
                enabled = month < currentMonth
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Tháng sau",
                    tint = if (month < currentMonth) {
                        colors.onSurface
                    } else {
                        colors.onSurface.copy(alpha = 0.4f)
                    }
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            containerColor = colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Chọn tháng",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            displayedYear--
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Năm trước",
                            tint = colors.onSurface
                        )
                    }

                    Text(
                        text = displayedYear.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )

                    IconButton(
                        onClick = {
                            if (displayedYear < currentMonth.year) {
                                displayedYear++
                            }
                        },
                        enabled = displayedYear < currentMonth.year
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Năm sau",
                            tint = if (displayedYear < currentMonth.year) {
                                colors.onSurface
                            } else {
                                colors.onSurface.copy(alpha = 0.4f)
                            }
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    (1..12).toList().chunked(3).forEach { rowMonths ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowMonths.forEach { monthNumber ->
                                val candidateMonth = YearMonth.of(
                                    displayedYear,
                                    monthNumber
                                )

                                val isSelected = candidateMonth == draftMonth
                                val isCurrent = candidateMonth == currentMonth
                                val isEnabled = candidateMonth <= currentMonth

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            color = when {
                                                isSelected -> colors.primary
                                                else -> colors.surfaceVariant
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable(enabled = isEnabled) {
                                            draftMonth = candidateMonth
                                        }
                                        .padding(vertical = 14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Tháng $monthNumber",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = when {
                                            isSelected -> colors.onPrimary
                                            isEnabled -> colors.onSurface
                                            else -> colors.onSurface.copy(alpha = 0.3f)
                                        }
                                    )

                                    if (isCurrent) {
                                        Text(
                                            text = "Hiện tại",
                                            fontSize = 10.sp,
                                            color = if (isSelected) {
                                                colors.onPrimary.copy(alpha = 0.85f)
                                            } else {
                                                colors.primary
                                            }
                                        )
                                    }
                                }
                            }

                            repeat(3 - rowMonths.size) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {}
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                            onMonthSelected(draftMonth)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Text(
                        text = "Xác nhận",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthSelectorPreview() {
    ZbudgetTheme {
        MonthSelector(
            month = YearMonth.of(2026, 11),
            onPreviousMonth = {},
            onNextMonth = {},
            onMonthSelected = {}
        )
    }
}
