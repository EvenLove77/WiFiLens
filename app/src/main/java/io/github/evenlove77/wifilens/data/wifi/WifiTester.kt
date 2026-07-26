package io.github.evenlove77.wifilens.data.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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

    suspend fun tryPassword(context: Context, ssid: String, password: String): Boolean {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return suspendCancellableCoroutine { cont ->
            var resolved = false
            val connectionJob = GlobalScope.launch {
                try {
                    val config = WifiConfiguration().apply {
                        SSID = "\"$ssid\""
                        preSharedKey = "\"$password\""
                        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                    }

                    @Suppress("DEPRECATION")
                    val netId = wm.addNetwork(config)
                    Log.d(TAG, "addNetwork: netId=$netId for $ssid")
                    if (netId == -1) { cont.resume(false); return@launch }

                    // 注册网络回调——onAvailable 表示 WiFi 已连接
                    var cbResolved = false
                    val cb = object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            Log.d(TAG, "onAvailable fired for $ssid")
                            if (!cbResolved && !resolved) {
                                cbResolved = true
                                try { cm.unregisterNetworkCallback(this) } catch (_: Exception) {}
                                Log.d(TAG, "connected! $ssid")
                                resolved = true
                                cont.resume(true)
                            }
                        }
                    }
                    cm.registerNetworkCallback(
                        NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(), cb
                    )

                    @Suppress("DEPRECATION")
                    wm.disconnect()
                    delay(300)

                    @Suppress("DEPRECATION")
                    wm.enableNetwork(netId, true)
                    @Suppress("DEPRECATION")
                    wm.reconnect()
                    Log.d(TAG, "reconnect called for $ssid")

                    // 等 4 秒
                    delay(4000)

                    if (!resolved) {
                        resolved = true
                        try { cm.unregisterNetworkCallback(cb) } catch (_: Exception) {}
                        @Suppress("DEPRECATION")
                        wm.removeNetwork(netId)
                        cont.resume(false)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "error: ${e.message}")
                    if (!resolved) { resolved = true; cont.resume(false) }
                }
            }

            cont.invokeOnCancellation {
                resolved = true
                connectionJob.cancel()
            }
        }
    }
}
