package io.github.evenlove77.wifilens.data.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSuggestion
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object WifiTester {

    private const val TAG = "WifiTester"

    suspend fun tryPassword(context: Context, ssid: String, password: String): Boolean {
        return suspendCancellableCoroutine { cont ->
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wm = context.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager

            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()

            var resolved = false

            val cb = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                    // 用 transportInfo 获取 SSID（API 33+，比 connectionInfo 更可靠）
                    val wifiInfo = caps.transportInfo as? android.net.wifi.WifiInfo
                    val connectedSsid = wifiInfo?.ssid?.removeSurrounding("\"") ?: ""
                    Log.d(TAG, "caps SSID: '$connectedSsid', target: '$ssid'")
                    if (!resolved && connectedSsid == ssid) {
                        resolved = true; cleanup(); cont.resume(true)
                    }
                }

                override fun onAvailable(network: Network) {
                    // 兜底：用 connectionInfo
                    @Suppress("DEPRECATION")
                    val info = wm.connectionInfo
                    val connectedSsid = info?.ssid?.removeSurrounding("\"") ?: ""
                    Log.d(TAG, "available SSID: '$connectedSsid', target: '$ssid'")
                    if (!resolved && connectedSsid == ssid) {
                        resolved = true; cleanup(); cont.resume(true)
                    }
                }

                fun cleanup() {
                    try { cm.unregisterNetworkCallback(this) } catch (_: Exception) {}
                    try { wm.removeNetworkSuggestions(listOf(suggestion)) } catch (_: Exception) {}
                }
            }

            cm.registerNetworkCallback(
                NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(), cb
            )

            try {
                val status = wm.addNetworkSuggestions(listOf(suggestion))
                Log.d(TAG, "suggest $ssid status=$status")
            } catch (e: Exception) {
                cm.unregisterNetworkCallback(cb)
                cont.resume(false)
                return@suspendCancellableCoroutine
            }

            cont.invokeOnCancellation {
                cm.unregisterNetworkCallback(cb)
                wm.removeNetworkSuggestions(listOf(suggestion))
            }

            // 轮询兜底：每 0.5 秒检查 connectionInfo（共 8 秒）
            kotlinx.coroutines.GlobalScope.launch {
                repeat(16) {
                    delay(500)
                    if (resolved) return@launch
                    @Suppress("DEPRECATION")
                    val info = wm.connectionInfo
                    val connectedSsid = info?.ssid?.removeSurrounding("\"") ?: ""
                    if (connectedSsid.isNotBlank()) {
                        Log.d(TAG, "poll SSID: '$connectedSsid', target: '$ssid'")
                    }
                    if (!resolved && connectedSsid == ssid) {
                        resolved = true
                        cm.unregisterNetworkCallback(cb)
                        wm.removeNetworkSuggestions(listOf(suggestion))
                        cont.resume(true)
                        return@launch
                    }
                }
                if (!resolved) {
                    resolved = true
                    cm.unregisterNetworkCallback(cb)
                    wm.removeNetworkSuggestions(listOf(suggestion))
                    if (cont.isActive) cont.resume(false)
                }
            }
        }
    }
}