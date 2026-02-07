package com.llucs.motioncues

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = SettingsDataStore(this)

        setContent {
            MotionCuesTheme {
                val navController = rememberNavController()

                var dotColor by remember { mutableStateOf(0xFFFFFFFF) }
                var dotCount by remember { mutableStateOf(10) }
                var dotSize by remember { mutableStateOf(2) }
                var isEffectActive by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    dotColor = dataStore.getDotColor()
                    dotCount = dataStore.getDotCount()
                    dotSize = dataStore.getDotSize()
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = false,
                                onClick = { navController.navigate("home") },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                                label = { Text("Início") }
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { navController.navigate("settings") },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Configurações") },
                                label = { Text("Config") }
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { navController.navigate("about") },
                                icon = { Icon(Icons.Default.Info, contentDescription = "Sobre") },
                                label = { Text("Sobre") }
                            )
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") {
                                HomeScreen(
                                    dataStore = dataStore,
                                    onToggleEffect = { isEffectActive = !isEffectActive }
                                )
                            }
                            composable("settings") {
                                SettingsScreen(dataStore)
                            }
                            composable("about") {
                                AboutScreen()
                            }
                        }

                        DotOverlayView(
                            dotColor = dotColor,
                            dotCount = dotCount,
                            dotSize = dotSize,
                            isEffectActive = isEffectActive,
                            sensorDetector = SensorDetector(this@MainActivity)
                        )
                    }
                }
            }
        }
    }
}