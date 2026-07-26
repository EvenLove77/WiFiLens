package io.github.evenlove77.wifilens.data.model

import androidx.compose.runtime.Immutable

/**
 * WiFi 网络扫描结果
 */
@Immutable
data class WiFiNetwork(
    val ssid: String,
    val bssid: String,
    val rssi: Int,           // 信号强度 dBm（例如 -40 到 -90）
    val frequency: Int,      // 频率 MHz（2400 或 5000+）
    val capabilities: String // 安全类型信息
) {
    /** 信号强度等级 0-3 */
    val signalLevel: Int
        get() = when {
            rssi >= -55 -> 3  // 强
            rssi >= -70 -> 2  // 中
            rssi >= -85 -> 1  // 弱
            else -> 0         // 极弱
        }

    /** 频段 */
    val band: String
        get() = if (frequency in 2400..2499) "2.4 GHz"
        else if (frequency in 5000..5899) "5 GHz"
        else if (frequency >= 5900) "6 GHz"
        else "Unknown"

    /** 频道 */
    val channel: Int
        get() = when {
            frequency in 2412..2484 -> (frequency - 2412) / 5 + 1
            frequency in 5035..5865 -> (frequency - 5035) / 5 + 7
            frequency >= 5955 -> (frequency - 5955) / 20 + 1
            else -> 0
        }

    /** 安全类型解析 */
    val securityType: String
        get() = when {
            capabilities.contains("WPA3") -> "WPA3"
            capabilities.contains("WPA2") -> "WPA2"
            capabilities.contains("WPA") -> "WPA"
            capabilities.contains("WEP") -> "WEP"
            capabilities.contains("OWE") -> "OWE"
            capabilities.contains("EAP") -> "WPA2-Enterprise"
            capabilities.contains("[ESS]") -> "Open"
            else -> "Unknown"
        }

    /** 信号百分比 0-100 */
    val signalPercent: Int
        get() = ((100.0 * (rssi + 100) / 60.0).toInt()).coerceIn(0, 100)
}
