package com.llucs.motioncues

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.llucs.motioncues.ui.theme.MotionCuesTheme
import com.llucs.motioncues.SensorDetectorImpl

class MainActivity : ComponentActivity() {

    private lateinit var dataStore: SettingsDataStore
    private lateinit var sensorDetector: SensorDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStore = SettingsDataStore(this)
        sensorDetector = SensorDetectorImpl(this)

        startMotionService()

        setContent {
            MotionCuesTheme {
                val isEffectActive = dataStore.effectActiveFlow.collectAsState(initial = false)
                val dotColor = dataStore.dotColorFlow.collectAsState(initial = Constants.DEFAULT_DOT_COLOR)
                val dotCount = dataStore.dotCountFlow.collectAsState(initial = Constants.DEFAULT_DOT_COUNT)
                val dotSize = dataStore.dotSizeFlow.collectAsState(initial = Constants.DEFAULT_DOT_SIZE)

                var selectedScreen by remember { mutableStateOf(0) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedScreen == 0,
                                onClick = { selectedScreen = 0 },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                                label = { Text("Início") }
                            )
                            NavigationBarItem(
                                selected = selectedScreen == 1,
                                onClick = { selectedScreen = 1 },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Configurações") },
                                label = { Text("Configurações") }
                            )
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                        DotOverlayView(
                            dotColor = dotColor.value.toLong(),
                            dotCount = dotCount.value,
                            dotSize = dotSize.value,
                            isEffectActive = isEffectActive.value,
                            sensorDetector = sensorDetector
                        )

                        when (selectedScreen) {
                            0 -> HomeScreen(dataStore)
                            1 -> SettingsScreen(dataStore)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (sensorDetector as SensorDetectorImpl).startDetection()
    }

    override fun onPause() {
        super.onPause()
        (sensorDetector as SensorDetectorImpl).stopDetection()
    }

    private fun startMotionService() {
        val intent = Intent(this, MotionService::class.java).apply {
            action = Constants.ACTION_START_SERVICE
        }
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopMotionService() {
        val intent = Intent(this, MotionService::class.java).apply {
            action = Constants.ACTION_STOP_SERVICE
        }
        stopService(intent)
    }
}