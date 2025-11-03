package com.llucs.motioncues

import android.content.Intent import android.os.Bundle import androidx.activity.ComponentActivity import androidx.activity.compose.setContent import androidx.compose.foundation.layout.Box import androidx.compose.foundation.layout.fillMaxSize import androidx.compose.material3.* import androidx.compose.runtime.collectAsState import androidx.compose.runtime.getValue import androidx.compose.ui.Modifier import androidx.core.content.ContextCompat import com.llucs.motioncues.ui.theme.MotionCuesTheme

class MainActivity : ComponentActivity() { private lateinit var dataStore: SettingsDataStore private lateinit var sensorDetector: SensorDetector

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    dataStore = SettingsDataStore(this)
    sensorDetector = SensorDetector(this)

    startMotionService()

    setContent {
        MotionCuesTheme {
            val isEffectActive by dataStore.effectActiveFlow.collectAsState(initial = false)
            val dotColor by dataStore.dotColorFlow.collectAsState(initial = Constants.DEFAULT_DOT_COLOR)
            val dotCount by dataStore.dotCountFlow.collectAsState(initial = Constants.DEFAULT_DOT_COUNT)
            val dotSize by dataStore.dotSizeFlow.collectAsState(initial = Constants.DEFAULT_DOT_SIZE)

            var selectedScreen = remember { mutableStateOf(0) }

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedScreen.value == 0,
                            onClick = { selectedScreen.value = 0 },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Início") }
                        )
                        NavigationBarItem(
                            selected = selectedScreen.value == 1,
                            onClick = { selectedScreen.value = 1 },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Config") },
                            label = { Text("Configurações") }
                        )
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    DotOverlayView(
                        dotColor = dotColor.toLong(),
                        dotCount = dotCount,
                        dotSize = dotSize,
                        isEffectActive = isEffectActive,
                        sensorDetector = sensorDetector
                    )

                    when (selectedScreen.value) {
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
    sensorDetector.startDetection()
}

override fun onPause() {
    super.onPause()
    sensorDetector.stopDetection()
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