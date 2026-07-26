package io.github.evenlove77.wifilens.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.evenlove77.wifilens.feature.detail.DetailScreen
import io.github.evenlove77.wifilens.feature.scan.ScanScreen
import io.github.evenlove77.wifilens.feature.settings.SettingsScreen
import java.net.URLDecoder

@Composable
fun WiFiLensNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Scan.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { it / 4 } + fadeIn(tween(300))
        },
        exitTransition = {
            slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it / 4 } + fadeOut(tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it / 4 } + fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it / 4 } + fadeOut(tween(300))
        }
    ) {
        composable(Screen.Scan.route) {
            ScanScreen(onNavigateToDetail = { ssid, bssid, rssi, freq, caps ->
                navController.navigate(Screen.Detail.createRoute(ssid, bssid, rssi, freq, caps))
            })
        }
        composable(Screen.Settings.route) { SettingsScreen() }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("ssid") { type = NavType.StringType },
                navArgument("bssid") { type = NavType.StringType },
                navArgument("rssi") { type = NavType.IntType },
                navArgument("frequency") { type = NavType.IntType },
                navArgument("capabilities") { type = NavType.StringType },
            )
        ) { entry ->
            val ssid = URLDecoder.decode(entry.arguments?.getString("ssid") ?: "", "UTF-8")
            val bssid = URLDecoder.decode(entry.arguments?.getString("bssid") ?: "", "UTF-8")
            val rssi = entry.arguments?.getInt("rssi") ?: 0
            val frequency = entry.arguments?.getInt("frequency") ?: 0
            val capabilities = URLDecoder.decode(entry.arguments?.getString("capabilities") ?: "", "UTF-8")
            DetailScreen(ssid, bssid, rssi, frequency, capabilities, onNavigateBack = { navController.popBackStack() })
        }
    }
}
