package io.github.evenlove77.wifilens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.evenlove77.wifilens.core.component.AppleBottomNavBar
import io.github.evenlove77.wifilens.core.theme.AppleTheme
import io.github.evenlove77.wifilens.core.theme.BackgroundDark
import io.github.evenlove77.wifilens.navigation.Screen
import io.github.evenlove77.wifilens.navigation.WiFiLensNavGraph

@Composable
fun WiFiLensApp(
    navController: NavHostController = rememberNavController()
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(Screen.Scan.route, Screen.Settings.route)

    AppleTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize().background(BackgroundDark),
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    AppleBottomNavBar(
                        currentRoute = currentRoute,
                        modifier = Modifier.navigationBarsPadding(),
                        onItemClick = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Scan.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                WiFiLensNavGraph(navController = navController)
            }
        }
    }
}
