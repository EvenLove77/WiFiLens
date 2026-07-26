package io.github.evenlove77.wifilens.core.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

/**
 * 雷达扫描动画 — 一条线绕中心旋转
 */
@Composable
fun rememberRadarRotation(
    durationMillis: Int = 3000
): Float {
    val transition = rememberInfiniteTransition(label = "radar")

    val rotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "radarRotation"
    )

    return rotation
}

/**
 * 扫描球呼吸动画
 */
@Composable
fun rememberBreathPulse(
    durationMillis: Int = 1500
): Float {
    val transition = rememberInfiniteTransition(label = "breath")

    val scale by transition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Reverse
        ), label = "breathScale"
    )

    return scale
}
