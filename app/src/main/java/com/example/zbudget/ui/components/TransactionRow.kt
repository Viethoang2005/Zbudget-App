
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

private data class CategoryStyle(
    val emoji: String,
    val lightBackground: Color,
    val darkBackground: Color,
    val lightForeground: Color,
    val darkForeground: Color
)

@Composable
fun TransactionRow(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF121713)

    val categoryStyle = getCategoryStyle(
        category = transaction.category,
        isIncome = isIncome
    )

    val categoryBackground = if (isDark) {
        categoryStyle.darkBackground
    } else {
        categoryStyle.lightBackground
    }

    val amountColor = if (isIncome) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

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
                        color = categoryBackground,
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
            lightBackground = Color(0xFFFFF0E5),
            darkBackground = Color(0xFF493326),
            lightForeground = Color(0xFFE58A36),
            darkForeground = Color(0xFFFFB875)
        )

        "Di chuyển" -> CategoryStyle(
            emoji = "🛵",
            lightBackground = Color(0xFFE8F1FF),
            darkBackground = Color(0xFF263B58),
            lightForeground = Color(0xFF3978D4),
            darkForeground = Color(0xFF91BFFF)
        )

        "Học tập" -> CategoryStyle(
            emoji = "📚",
            lightBackground = Color(0xFFF0E9FF),
            darkBackground = Color(0xFF392D50),
            lightForeground = Color(0xFF8054C7),
            darkForeground = Color(0xFFC4A5FF)
        )

        "Thu nhập" -> CategoryStyle(
            emoji = "💼",
            lightBackground = Color(0xFFE3F5E9),
            darkBackground = Color(0xFF244832),
            lightForeground = Color(0xFF218653),
            darkForeground = Color(0xFF8BD5A5)
        )

        "Gia đình" -> CategoryStyle(
            emoji = "🏠",
            lightBackground = Color(0xFFFFE9EE),
            darkBackground = Color(0xFF4B2D37),
            lightForeground = Color(0xFFD85D7B),
            darkForeground = Color(0xFFFFA6BB)
        )

        else -> CategoryStyle(
            emoji = if (isIncome) "💰" else "💳",
            lightBackground = if (isIncome) {
                Color(0xFFE3F5E9)
            } else {
                Color(0xFFF0F1F3)
            },
            darkBackground = if (isIncome) {
                Color(0xFF244832)
            } else {
                Color(0xFF30363D)
            },
            lightForeground = if (isIncome) {
                Color(0xFF218653)
            } else {
                Color(0xFF747985)
            },
            darkForeground = if (isIncome) {
                Color(0xFF8BD5A5)
            } else {
                Color(0xFFB8C0CA)
            }
        )
    }
}
