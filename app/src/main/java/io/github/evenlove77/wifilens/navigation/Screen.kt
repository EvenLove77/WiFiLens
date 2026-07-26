package io.github.evenlove77.wifilens.navigation

/**
 * 应用路由定义
 */
sealed class Screen(val route: String) {
    data object Scan : Screen("scan")
    data object Vault : Screen("vault")
    data object History : Screen("history")
    data object Settings : Screen("settings")
    data object Detail : Screen("detail/{ssid}") {
        fun createRoute(ssid: String) = "detail/$ssid"
    }
}
