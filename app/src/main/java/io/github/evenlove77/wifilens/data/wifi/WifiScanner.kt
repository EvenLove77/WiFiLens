package io.github.evenlove77.wifilens.data.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import io.github.evenlove77.wifilens.data.model.WiFiNetwork
import kotlinx.coroutines.delay

/**
 * WiFi 扫描器封装
 */
class WifiScanner(private val context: Context) {

    private val wifiManager: WifiManager? =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    fun hasPermission(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                context, Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        }
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

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

    /**
     * 异步 WiFi 扫描
     * 触发 startScan() 后等待扫描完成再读取结果
     */
    suspend fun scanAsync(): List<WiFiNetwork> {
        val wm = wifiManager ?: return emptyList()
        if (!hasPermission()) return emptyList()

        return try {
            @Suppress("DEPRECATION")
            wm.startScan()

            // 等待扫描完成（Android 14+ 扫描通常 1-3 秒）
            delay(2500)

            @Suppress("DEPRECATION")
            val allResults = wm.scanResults ?: emptyList()

            mapResults(allResults)
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    private fun mapResults(results: List<ScanResult>): List<WiFiNetwork> {
        return results
            .filter { !it.wifiSsid?.toString().isNullOrBlank() }
            .map { result ->
                WiFiNetwork(
                    ssid = result.wifiSsid?.toString()
                        ?: @Suppress("DEPRECATION") result.SSID.removeSurrounding("\""),
                    bssid = result.BSSID,
                    rssi = result.level,
                    frequency = result.frequency,
                    capabilities = result.capabilities
                )
            }
            .sortedByDescending { it.rssi }
            .distinctBy { it.bssid }
    }

    fun isWifiEnabled(): Boolean = wifiManager?.isWifiEnabled ?: false
}
