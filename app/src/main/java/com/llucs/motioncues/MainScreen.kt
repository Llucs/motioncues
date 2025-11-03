package com.llucs.motioncues

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.llucs.motioncues.ui.theme.MotionCuesTheme

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object About : Screen("about")
}

@Composable
fun MainScreen(onStartService: () -> Unit, onStopService: () -> Unit, dataStore: SettingsDataStore) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    MotionCuesTheme {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(currentRoute) { navController.navigate(it) }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(dataStore)
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(dataStore)
                }
                composable(Screen.About.route) {
                    AboutScreen()
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF101010))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(currentRoute == Screen.Home.route, Icons.Default.Home, "Home") {
            onNavigate(Screen.Home.route)
        }
        BottomNavItem(currentRoute == Screen.Settings.route, Icons.Default.Settings, "Config") {
            onNavigate(Screen.Settings.route)
        }
        BottomNavItem(currentRoute == Screen.About.route, Icons.Default.Info, "Sobre") {
            onNavigate(Screen.About.route)
        }
    }
}

@Composable
fun BottomNavItem(selected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .let { if (selected) it.background(Color(0xFF202020), CircleShape) else it }
            .padding(12.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = if (selected) Color.White else Color.Gray, modifier = Modifier.size(24.dp))
        Text(label, color = if (selected) Color.White else Color.Gray, fontSize = 12.sp)
    }
}