package com.llucs.motioncues

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.llucs.motioncues.ui.theme.MotionCuesTheme

private sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object About : Screen("about")
}

@Composable
fun MainScreen(
    dataStore: SettingsDataStore,
    onStartService: () -> Unit = {},
    onStopService: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    MotionCuesTheme {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    current = currentRoute,
                    onHome = { navController.navigate(Screen.Home.route) },
                    onSettings = { navController.navigate(Screen.Settings.route) },
                    onAbout = { navController.navigate(Screen.About.route) }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Home.route) { HomeScreen(dataStore) }
                composable(Screen.Settings.route) { SettingsScreen(dataStore) }
                composable(Screen.About.route) { AboutScreen() }
            }
        }
    }
}