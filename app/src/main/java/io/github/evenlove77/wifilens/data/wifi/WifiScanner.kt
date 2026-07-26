package io.github.evenlove77.wifilens.data.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.content.ContextCompat
import io.github.evenlove77.wifilens.data.model.WiFiNetwork
import kotlinx.coroutines.delay

data class ScanResult2(
    val networks: List<WiFiNetwork>,
    val error: String? = null
)

class WifiScanner(private val context: Context) {

    private val wifiManager: WifiManager? =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    companion object {
        private const val TAG = "WifiScanner"
    }

    fun hasPermission(): Boolean {
        val hasNearby = ContextCompat.checkSelfPermission(
            context, Manifest.permission.NEARBY_WIFI_DEVICES
        ) == PackageManager.PERMISSION_GRANTED
        val hasLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return hasNearby || hasLocation
    }

    fun getRequiredPermissions(): Array<String> {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    /**
     * WiFi 扫描
     * 策略：startScan() 触发扫描 → 等待 2 秒 → 读取结果
     * release 包完全可靠，debug/USB 调试可能被厂商拦截
     */
    suspend fun scanAsync(): ScanResult2 {
        val wm = wifiManager
        if (wm == null) return ScanResult2(emptyList(), "设备不支持 WiFi")
        if (!hasPermission()) return ScanResult2(emptyList(), "没有 WiFi 扫描权限")
        if (!wm.isWifiEnabled) return ScanResult2(emptyList(), "WiFi 未开启")

        try {
            @Suppress("DEPRECATION")
            wm.startScan()

            delay(2000)

            @Suppress("DEPRECATION")
            val raw = wm.scanResults ?: emptyList()

            val networks = raw
                .filter { r -> (r.SSID?.removeSurrounding("\"") ?: "").isNotBlank() }
                .map { r ->
                    WiFiNetwork(
                        ssid = r.SSID?.removeSurrounding("\"") ?: "",
                        bssid = r.BSSID,
                        rssi = r.level,
                        frequency = r.frequency,
                        capabilities = r.capabilities
                    )
                }
                .sortedByDescending { it.rssi }
                .distinctBy { it.bssid }

            Log.d(TAG, "扫描到 ${networks.size} 个网络")
            if (networks.isEmpty()) {
                return ScanResult2(emptyList(), "未扫描到 WiFi 网络")
            }
            return ScanResult2(networks)
        } catch (e: SecurityException) {
            return ScanResult2(emptyList(), "权限被拒绝")
        } catch (e: Exception) {
            return ScanResult2(emptyList(), "扫描失败: ${e.message}")
        }
    }

    fun isWifiEnabled(): Boolean = wifiManager?.isWifiEnabled ?: false
}
