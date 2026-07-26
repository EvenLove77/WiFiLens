package io.github.evenlove77.wifilens.data.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import io.github.evenlove77.wifilens.data.model.WiFiNetwork

/**
 * WiFi 扫描器封装
 * 封装 Android WifiManager，返回 cleaned data
 */
class WifiScanner(private val context: Context) {

    private val wifiManager: WifiManager? =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    /** 检查是否有 WiFi 权限 */
    fun hasPermission(): Boolean {
        // Android 14+ 用 NEARBY_WIFI_DEVICES
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                context, Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        }
        // Android 13 及以下需要位置权限
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** 获取权限列表（用于动态请求） */
    fun getRequiredPermissions(): Array<String> {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    /** 执行 WiFi 扫描，返回排序后的结果列表 */
    fun scan(): List<WiFiNetwork> {
        val wm = wifiManager ?: return emptyList()

        if (!hasPermission()) return emptyList()

        val results: List<ScanResult> = try {
            if (!wm.isWifiEnabled) {
                // 如果 WiFi 关闭，先开启（用户需理解）
            }
            wm.scanResults ?: emptyList()
        } catch (e: SecurityException) {
            return emptyList()
        }

        return results
            .filter { it.wifiSsid?.toString().isNullOrBlank().not() }
            .map { result ->
                WiFiNetwork(
                    ssid = result.wifiSsid?.toString() ?: result.SSID.removeSurrounding("\""),
                    bssid = result.BSSID,
                    rssi = result.level,
                    frequency = result.frequency,
                    capabilities = result.capabilities
                )
            }
            .sortedByDescending { it.rssi } // 信号最强的排前面
            .distinctBy { it.bssid }        // 按 BSSID 去重
    }

    /** 检查 WiFi 是否已开启 */
    fun isWifiEnabled(): Boolean = wifiManager?.isWifiEnabled ?: false
}
