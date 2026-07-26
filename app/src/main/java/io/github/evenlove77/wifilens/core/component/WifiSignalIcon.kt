package io.github.evenlove77.wifilens.core.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SignalWifi0Bar
import androidx.compose.material.icons.rounded.SignalWifi4Bar
import androidx.compose.material.icons.rounded.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.evenlove77.wifilens.core.theme.*

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
    Icon(
        imageVector = Icons.Rounded.Wifi,
        contentDescription = "WiFi信号",
        tint = activeColor,
        modifier = modifier.size(size)
    )
}

@Composable
fun WifiSignalIconLarge(
    signalLevel: Int,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    WifiSignalIcon(signalLevel = signalLevel, modifier = modifier, size = size)
}
