package io.github.evenlove77.wifilens.core.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.evenlove77.wifilens.core.theme.*

/**
 * Canvas 绘制 WiFi 信号图标
 * 3 条圆弧，根据 signalLevel (0-3) 点亮
 */
@Composable
fun WifiSignalIcon(
    signalLevel: Int,  // 0=弱 1=一般 2=良好 3=强
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    activeColor: Color = when (signalLevel) {
        3 -> SignalExcellent
        2 -> SignalGood
        1 -> SignalFair
        else -> SignalWeak
    },
    inactiveColor: Color = TextTertiary.copy(alpha = 0.3f)
) {
    Canvas(
        modifier = modifier.size(size)
    ) {
        val strokeWidth = size.toPx() * 0.15f
        val centerX = size.toPx() / 2f
        val centerY = size.toPx() * 0.75f

        // 三层圆弧，从小到达
        val arcs = listOf(
            // 内弧（信号弱，仅点亮 1 条表示至少有点信号）
            Triple(centerX - size.toPx() * 0.15f, size.toPx() * 0.35f, 45f),
            // 中弧
            Triple(centerX - size.toPx() * 0.3f, size.toPx() * 0.28f, 60f),
            // 外弧
            Triple(centerX - size.toPx() * 0.44f, size.toPx() * 0.22f, 72f),
        )

        arcs.forEachIndexed { index, (left, arcSize, sweepAngle) ->
            val isActive = index < signalLevel
            val color = if (isActive) activeColor else inactiveColor

            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(left, 0f),
                size = Size(arcSize, arcSize),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // 底部圆点
        drawCircle(
            color = activeColor,
            radius = size.toPx() * 0.1f,
            center = Offset(centerX, centerY)
        )
    }
}

@Composable
fun WifiSignalIconLarge(
    signalLevel: Int,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    WifiSignalIcon(
        signalLevel = signalLevel,
        modifier = modifier,
        size = size
    )
}
