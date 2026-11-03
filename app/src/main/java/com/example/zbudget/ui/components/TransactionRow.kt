
package com.example.zbudget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetRed

private data class CategoryStyle(
    val emoji: String,
    val background: Color,
    val foreground: Color
)

@Composable
fun TransactionRow(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INCOME

    val categoryStyle = getCategoryStyle(
        category = transaction.category,
        isIncome = isIncome
    )

    val amountColor = if (isIncome) ZbudgetGreen else ZbudgetRed
    val amountPrefix = if (isIncome) "+" else "−"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = categoryStyle.background,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = categoryStyle.emoji,
                    fontSize = 24.sp
                )
            }


            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row {
                    Text(
                        text = transaction.timeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.75f
                        )
                    )
                    Text(
                        text = " - ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.75f
                        )
                    )
                    Text(
                        text = transaction.dateLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.75f
                        )
                    )
                }
            }


            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "$amountPrefix${formatVnd(transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                    maxLines = 1
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isIncome) "Thu nhập" else "Chi tiêu",
                        style = MaterialTheme.typography.labelSmall,
                        color = amountColor
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Xem chi tiết",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.6f
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun getCategoryStyle(
    category: String,
    isIncome: Boolean
): CategoryStyle {
    return when (category) {
        "Ăn uống" -> CategoryStyle(
            emoji = "🍜",
            background = Color(0xFFFFF0E5),
            foreground = Color(0xFFE58A36)
        )

        "Di chuyển" -> CategoryStyle(
            emoji = "🛵",
            background = Color(0xFFE8F1FF),
            foreground = Color(0xFF3978D4)
        )

        "Học tập" -> CategoryStyle(
            emoji = "📚",
            background = Color(0xFFF0E9FF),
            foreground = Color(0xFF8054C7)
        )

        "Thu nhập" -> CategoryStyle(
            emoji = "💼",
            background = Color(0xFFE3F5E9),
            foreground = ZbudgetGreen
        )

        "Gia đình" -> CategoryStyle(
            emoji = "🏠",
            background = Color(0xFFFFE9EE),
            foreground = Color(0xFFD85D7B)
        )

        else -> CategoryStyle(
            emoji = if (isIncome) "💰" else "💳",
            background = if (isIncome) {
                Color(0xFFE3F5E9)
            } else {
                Color(0xFFF0F1F3)
            },
            foreground = if (isIncome) {
                ZbudgetGreen
            } else {
                Color(0xFF747985)
            }
        )
    }
}

