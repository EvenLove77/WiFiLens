package io.github.evenlove77.wifilens.navigation

sealed class Screen(val route: String) {
    data object Scan : Screen("scan")
    data object Settings : Screen("settings")
    data object Detail : Screen("detail/{ssid}/{bssid}/{rssi}/{frequency}/{capabilities}") {
        fun createRoute(ssid: String, bssid: String, rssi: Int, frequency: Int, capabilities: String): String {
            return "detail/${ssid.encode()}/${bssid.encode()}/$rssi/$frequency/${capabilities.encode()}"
        }
    }
}

private fun String.encode(): String = java.net.URLEncoder.encode(this, "UTF-8")
