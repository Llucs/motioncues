package com.llucs.motioncues

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.llucs.motioncues.ui.theme.MotionCuesTheme

class MainActivity : ComponentActivity() {

    private lateinit var dataStore: SettingsDataStore
    private lateinit var sensorDetector: SensorDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = SettingsDataStore(this)
        sensorDetector = SensorDetector(this)

        // Iniciar o serviço em primeiro plano ao abrir o app (se ainda não estiver rodando)
        startMotionService()

        setContent {
            MotionCuesTheme {
                val isEffectActive by dataStore.effectActiveFlow.collectAsState(initial = false)
                val dotColor by dataStore.dotColorFlow.collectAsState(initial = Constants.DEFAULT_DOT_COLOR)
                val dotCount by dataStore.dotCountFlow.collectAsState(initial = Constants.DEFAULT_DOT_COUNT)
                val dotSize by dataStore.dotSizeFlow.collectAsState(initial = Constants.DEFAULT_DOT_SIZE)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // O DotOverlayView é colocado sobre o conteúdo principal do app
                    DotOverlayView(
                        dotColor = dotColor.toLong(),
                        dotCount = dotCount,
                        dotSize = dotSize,
                        isEffectActive = isEffectActive,
                        sensorDetector = sensorDetector
                    )
                    // Conteúdo principal do app (Navegação entre Configurações e Sobre)
                    MainScreen(
                        onStartService = { startMotionService() },
                        onStopService = { stopMotionService() },
                        dataStore = dataStore
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Retomar a detecção de sensores quando o app volta ao primeiro plano
        sensorDetector.startDetection()
    }

    override fun onPause() {
        super.onPause()
        // Pausar a detecção de sensores quando o app vai para o fundo
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
