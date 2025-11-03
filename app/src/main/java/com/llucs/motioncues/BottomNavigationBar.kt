package com.llucs.motioncues

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationBar(
    current: String,
    onHome: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = current == "home",
            onClick = onHome,
            icon = { Icon(Icons.Filled.Home, "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = current == "settings",
            onClick = onSettings,
            icon = { Icon(Icons.Filled.Settings, "Config") },
            label = { Text("Config") }
        )
        NavigationBarItem(
            selected = current == "about",
            onClick = onAbout,
            icon = { Icon(Icons.Filled.Info, "Sobre") },
            label = { Text("Sobre") }
        )
    }
}