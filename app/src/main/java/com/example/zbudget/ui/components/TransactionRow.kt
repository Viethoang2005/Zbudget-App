package com.example.zbudget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetRed
import com.example.zbudget.ui.theme.ZbudgetSecondary
import com.example.zbudget.ui.theme.ZbudgetText

@Composable
fun TransactionRow(transaction: Transaction) {
    val isIncome = transaction.type == TransactionType.INCOME
    val iconBackground = if (isIncome) ZbudgetGreenLight else Color(0xFFFFEEEE)
    val amountColor = if (isIncome) ZbudgetGreen else ZbudgetRed
    val amountPrefix = if (isIncome) "+" else "−"
    val symbol = when (transaction.category) {
        "Ăn uống" -> "🍜"
        "Di chuyển" -> "🛵"
        "Học tập" -> "📚"
        "Thu nhập" -> "💼"
        "Gia đình" -> "🏠"
        else -> "💳"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = symbol, fontSize = 21.sp)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = transaction.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = ZbudgetText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${transaction.category} • ${transaction.dateLabel}",
                fontSize = 12.sp,
                color = ZbudgetSecondary
            )
        }

        Text(
            text = "$amountPrefix${formatVnd(transaction.amount)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}