package com.llucs.motioncues

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.llucs.motioncues.ui.theme.MotionCuesTheme

class MainActivity : ComponentActivity() {

    private lateinit var dataStore: SettingsDataStore
    private lateinit var sensorDetector: SensorDetector

    private val requiredPermissions = arrayOf(
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.ACCESS_FINE_LOCATION // se precisar, por exemplo
    )
    private val PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = SettingsDataStore(this)

        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        initSensorsAndUI()
    }

    private fun initSensorsAndUI() {
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
                    try {
                        DotOverlayView(
                            dotColor = dotColor.toLong(),
                            dotCount = dotCount,
                            dotSize = dotSize,
                            isEffectActive = isEffectActive,
                            sensorDetector = sensorDetector
                        )
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro nos sensores: ${e.message}", Toast.LENGTH_LONG).show()
                    }

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
        if (::sensorDetector.isInitialized) {
            sensorDetector.startDetection()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::sensorDetector.isInitialized) {
            sensorDetector.stopDetection()
        }
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

    private fun checkPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initSensorsAndUI()
            } else {
                Toast.makeText(this, "O app precisa de todas as permissões para funcionar", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}