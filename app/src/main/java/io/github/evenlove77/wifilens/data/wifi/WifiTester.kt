package io.github.evenlove77.wifilens.data.wifi

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object WifiTester {

    private const val TAG = "WifiTester"

    /**
     * 用 addNetwork + enableNetwork 直接连接（WiFi万能钥匙同款做法）
     */
    suspend fun tryPassword(context: Context, ssid: String, password: String): Boolean {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        return suspendCancellableCoroutine { cont ->
            var resolved = false

            // 在后台线程执行连接逻辑
            kotlinx.coroutines.GlobalScope.launch {
                try {
                    val config = WifiConfiguration().apply {
                        SSID = "\"$ssid\""
                        preSharedKey = "\"$password\""
                        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                    }

                    @Suppress("DEPRECATION")
                    val netId = wm.addNetwork(config)
                    Log.d(TAG, "addNetwork: netId=$netId for $ssid")

                    if (netId == -1) {
                        Log.e(TAG, "addNetwork failed (OEM blocked)")
                        cont.resume(false)
                        return@launch
                    }

                    @Suppress("DEPRECATION")
                    wm.disconnect()
                    delay(200)

                    @Suppress("DEPRECATION")
                    wm.enableNetwork(netId, true)
                    @Suppress("DEPRECATION")
                    wm.reconnect()

                    // 等 4 秒看是否连上
                    repeat(8) {
                        delay(500)
                        if (resolved) return@launch
                        @Suppress("DEPRECATION")
                        val info = wm.connectionInfo
                        val curSsid = info?.ssid?.removeSurrounding("\"") ?: ""
                        val curNetId = info?.networkId ?: -1
                        Log.d(TAG, "poll: curSsid=$curSsid curNetId=$curNetId target=$ssid netId=$netId")
                        if (curNetId == netId && curSsid == ssid) {
                            Log.d(TAG, "connected! $ssid")
                            resolved = true
                            cont.resume(true)
                            return@launch
                        }
                    }
                    if (!resolved) {
                        resolved = true
                        @Suppress("DEPRECATION")
                        wm.removeNetwork(netId)
                        cont.resume(false)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "error: ${e.message}")
                    cont.resume(false)
                }
            }

            cont.invokeOnCancellation {
                resolved = true
            }
        }
    }
}
