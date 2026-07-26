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
 * 波纹扩散动画 — 从中心向外扩散的圆环脉冲
 */
@Composable
fun rememberRipplePulse(
    durationMillis: Int = 2000,
    delayPerRing: Int = 600
): List<Float> {
    val transition = rememberInfiniteTransition(label = "ripple")

    // 4 层波纹，每层延迟 600ms
    val ring1 by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "ring1"
    )
    val ring2 by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, delayPerRing, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "ring2"
    )
    val ring3 by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, delayPerRing * 2, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "ring3"
    )
    val ring4 by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, delayPerRing * 3, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "ring4"
    )

    return listOf(ring1, ring2, ring3, ring4)
}
