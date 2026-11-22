package com.example.zbudget.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenLight

@Composable
fun ZbudgetLoading(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    strokeWidth: Dp = 3.dp
) {
    val transition = rememberInfiniteTransition(label = "loading")

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val radius = (this.size.minDimension - stroke) / 2

            drawCircle(
                color = ZbudgetGreenLight,
                radius = radius,
                style = Stroke(width = stroke)
            )

            drawArc(
                color = ZbudgetGreen,
                startAngle = rotation,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = androidx.compose.ui.geometry.Size(
                    this.size.width - stroke,
                    this.size.height - stroke
                ),
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Preview
@Composable
fun ZbudgetLoadingPreview() {
    ZbudgetLoading()
}