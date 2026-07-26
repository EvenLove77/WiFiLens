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

    suspend fun scanAsync(): ScanResult2 {
        val wm = wifiManager
        if (wm == null) return ScanResult2(emptyList(), "设备不支持 WiFi")
        if (!hasPermission()) return ScanResult2(emptyList(), "没有 WiFi 扫描权限")
        if (!wm.isWifiEnabled) return ScanResult2(emptyList(), "WiFi 未开启，请打开 WiFi 后重试")

        // 轮询等待系统扫描结果
        var attempts = 0
        while (attempts < 5) {
            @Suppress("DEPRECATION")
            val raw = wm.scanResults
            val count = raw?.size ?: 0
            Log.d(TAG, "poll $attempts: count=$count")

            if (count > 0) {
                val networks = mapResults(raw!!)
                Log.d(TAG, "found ${networks.size} networks")
                return ScanResult2(networks)
            }

            if (attempts == 0) {
                @Suppress("DEPRECATION")
                wm.startScan() // fire & forget (vivo ignores, standard Android works)
            }
            delay(2000)
            attempts++
        }
        return ScanResult2(emptyList(), "未扫描到 WiFi 网络，请稍后重试")
    }

    private fun mapResults(results: List<ScanResult>): List<WiFiNetwork> {
        return results
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
    }

    fun isWifiEnabled(): Boolean = wifiManager?.isWifiEnabled ?: false
}
