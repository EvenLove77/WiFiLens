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
import io.github.evenlove77.wifilens.feature.history.HistoryScreen
import io.github.evenlove77.wifilens.feature.scan.ScanScreen
import io.github.evenlove77.wifilens.feature.settings.SettingsScreen
import io.github.evenlove77.wifilens.feature.vault.VaultScreen

/**
 * 应用导航图
 * iOS 风格过渡动画：
 * - push: 从右滑入 + fadeIn
 * - pop: 向左滑出 + fadeOut
 */
@Composable
fun WiFiLensNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Scan.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                initialOffsetX = { it / 4 }  // 从右侧 1/4 屏幕外滑入
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                targetOffsetX = { -it / 4 }  // 向左滑出
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                initialOffsetX = { -it / 4 }  // 从左侧滑入（返回时）
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                targetOffsetX = { it / 4 }    // 向右滑出（返回时）
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(route = Screen.Scan.route) {
            ScanScreen(onNavigateToDetail = { ssid ->
                navController.navigate(Screen.Detail.createRoute(ssid))
            })
        }

        composable(route = Screen.Vault.route) {
            VaultScreen()
        }

        composable(route = Screen.History.route) {
            HistoryScreen()
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("ssid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val ssid = backStackEntry.arguments?.getString("ssid") ?: ""
            DetailScreen(
                ssid = ssid,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
