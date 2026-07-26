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
    val error: String? = null  // null = success, non-null = what went wrong
)

class WifiScanner(private val context: Context) {

    private val wifiManager: WifiManager? =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    companion object {
        private const val TAG = "WifiScanner"
    }

    fun hasPermission(): Boolean {
        // 同时检查 NEARBY_WIFI_DEVICES 和位置（vivo 等厂商可能需要两者）
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
            arrayOf(
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    suspend fun scanAsync(): ScanResult2 {
        val wm = wifiManager
        if (wm == null) {
            Log.e(TAG, "设备不支持 WiFi")
            return ScanResult2(emptyList(), "设备不支持 WiFi")
        }

        if (!hasPermission()) {
            Log.e(TAG, "无 WiFi 扫描权限")
            return ScanResult2(emptyList(), "没有 WiFi 扫描权限")
        }

        // WiFi 关闭时扫描也无效
        if (!wm.isWifiEnabled) {
            Log.w(TAG, "WiFi 未开启")
            return ScanResult2(emptyList(), "WiFi 未开启，请打开 WiFi 后重试")
        }

        return try {
            // vivo 手机会拦截 startScan()，改用轮询等待系统扫描结果
            var attempts = 0
            var rawResults: List<ScanResult>? = null

            while (attempts < 6) {
                @Suppress("DEPRECATION")
                rawResults = wm.scanResults
                val count = rawResults?.size ?: 0
                Log.d(TAG, "attempt $attempts: scanResults count=$count")

                if (count > 0) break

                // 第一次尝试触发扫描（即使被拦截也无所谓，继续轮询）
                if (attempts == 0) {
                    @Suppress("DEPRECATION")
                    val triggered = wm.startScan()
                    Log.d(TAG, "startScan triggered=$triggered")
                }

                delay(2000)
                attempts++
            }

            @Suppress("DEPRECATION")
            val results = rawResults?.let { mapResults(it) } ?: emptyList()

            if (results.isEmpty()) {
                Log.w(TAG, "扫描结果为空（vivo 可能限制了 WiFi 扫描）")
                ScanResult2(emptyList(), "未扫描到 WiFi。请尝试：打开系统WiFi设置页面后再返回App")
            } else {
                Log.d(TAG, "扫描到 ${results.size} 个 WiFi")
                ScanResult2(results)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "权限异常: ${e.message}")
            ScanResult2(emptyList(), "权限被拒绝")
        } catch (e: Exception) {
            Log.e(TAG, "扫描异常: ${e.message}")
            ScanResult2(emptyList(), "扫描失败: ${e.message}")
        }
    }

    private fun mapResults(results: List<ScanResult>): List<WiFiNetwork> {
        return results
            .filter { result ->
                // 兼容新旧 API：wifiSsid 可能为 null，回退到 SSID
                val ssid = result.wifiSsid?.toString()
                    ?: @Suppress("DEPRECATION") result.SSID.removeSurrounding("\"")
                ssid.isNotBlank()
            }
            .map { result ->
                val ssid = result.wifiSsid?.toString()
                    ?: @Suppress("DEPRECATION") result.SSID.removeSurrounding("\"")
                WiFiNetwork(
                    ssid = ssid,
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
