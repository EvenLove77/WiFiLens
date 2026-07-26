package io.github.evenlove77.wifilens.data.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.util.Log
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * WiFi 密码测试器
 * 用 WifiNetworkSpecifier 尝试连接，测试密码是否正确
 */
object WifiTester {

    private const val TAG = "WifiTester"

    /**
     * 尝试用给定密码连接 WiFi，返回是否成功
     * 注意：Android 系统会弹出授权对话框，无法静默
     */
    suspend fun tryPassword(context: Context, ssid: String, password: String): Boolean {
        return suspendCoroutine { cont ->
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val specifier = WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()

            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier)
                .build()

            var done = false

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d(TAG, "连接成功: $ssid -> $password")
                    if (!done) {
                        done = true
                        connectivityManager.unregisterNetworkCallback(this)
                        cont.resume(true)
                    }
                }

                override fun onUnavailable() {
                    Log.d(TAG, "连接失败: $ssid -> $password")
                    if (!done) {
                        done = true
                        connectivityManager.unregisterNetworkCallback(this)
                        cont.resume(false)
                    }
                }

                override fun onLost(network: Network) {
                    // 连接后立即断开也算失败
                }
            }

            try {
                connectivityManager.requestNetwork(request, callback)
            } catch (e: Exception) {
                Log.e(TAG, "requestNetwork 异常: ${e.message}")
                if (!done) {
                    done = true
                    cont.resume(false)
                }
            }
        }
    }
}
