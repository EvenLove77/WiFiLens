package io.github.evenlove77.wifilens.core.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.evenlove77.wifilens.core.animation.rememberBreathPulse
import io.github.evenlove77.wifilens.core.animation.rememberRadarRotation
import io.github.evenlove77.wifilens.core.animation.rememberRipplePulse
import io.github.evenlove77.wifilens.core.theme.*

/**
 * 液态玻璃扫描球
 * 中心蓝色光晕球体 + 波纹扩散 + 雷达扫描线
 */
@Composable
fun LiquidGlassScanBall(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true,
    ballSize: Dp = IconSizeScanBall
) {
    val rings = rememberRipplePulse(durationMillis = 2500, delayPerRing = 700)
    val radarRotation = rememberRadarRotation(durationMillis = 3500)
    val breathScale = rememberBreathPulse(durationMillis = 2000)
    val density = LocalDensity.current

    // Convert Dp to pixels for Canvas operations
    val ballSizePx = with(density) { ballSize.toPx() }

    Box(
        modifier = modifier.size(ballSize),
        contentAlignment = Alignment.Center
    ) {
        // 外层光晕
        Canvas(
            modifier = Modifier
                .size(ballSize * 0.9f)
                .scale(breathScale)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AppleBlue.copy(alpha = 0.15f),
                        AppleBlue.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.minDimension / 2f
                )
            )
        }

        // 波纹扩散圆环
        if (isScanning) {
            rings.forEachIndexed { index, progress ->
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val baseRadius = size.minDimension * 0.2f
                    val maxExtra = size.minDimension * 0.35f
                    val radius = baseRadius + (maxExtra * progress)
                    val alpha = (1f - progress) * 0.4f

                    drawCircle(
                        color = AppleBlue.copy(alpha = alpha),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 1.5f.dp.toPx())
                    )
                }
            }
        }

        // 雷达扫描扇区
        if (isScanning) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                rotate(radarRotation, center) {
                    drawArc(
                        color = AppleCyan.copy(alpha = 0.12f),
                        startAngle = -30f,
                        sweepAngle = 40f,
                        useCenter = true,
                        topLeft = Offset(
                            size.minDimension * 0.1f,
                            size.minDimension * 0.1f
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            size.minDimension * 0.8f,
                            size.minDimension * 0.8f
                        )
                    )
                    // 扫描线
                    drawLine(
                        color = AppleCyan.copy(alpha = 0.4f),
                        start = center,
                        end = Offset(center.x, center.y - size.minDimension * 0.38f),
                        strokeWidth = 1f.dp.toPx()
                    )
                }
            }
        }

        // 中心玻璃球
        Box(
            modifier = Modifier
                .size(ballSize * 0.38f)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            AppleBlue.copy(alpha = 0.25f),
                            AppleBlue.copy(alpha = 0.08f)
                        ),
                        center = Offset(0.35f, 0.3f),
                        radius = Float.POSITIVE_INFINITY
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // 内亮点
            Box(
                modifier = Modifier
                    .size(ballSize * 0.1f)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            center = Offset(0.3f, 0.25f)
                        )
                    )
            )
        }
    }
}
