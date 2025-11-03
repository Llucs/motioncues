package com.llucs.motioncues

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sqrt

class SensorDetector(private val context: Context) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // Sensores
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val linearAcceleration: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    // Estados
    private val _isMovingInVehicle = MutableStateFlow(false)
    val isMovingInVehicle: StateFlow<Boolean> = _isMovingInVehicle

    // Fluxos de dados dos sensores para uso em outras partes do app
    private val _accelerometerData = MutableStateFlow(FloatArray(3))
    val accelerometerData: StateFlow<FloatArray> = _accelerometerData

    private val _gyroscopeData = MutableStateFlow(FloatArray(3))
    val gyroscopeData: StateFlow<FloatArray> = _gyroscopeData

    private var lastLocation: Location? = null
    private var vehicleDetectionStartTime: Long = 0
    private var lastVehicleDetectionTime: Long = 0

    // Variáveis para análise de aceleração e vibração
    private var accelerationMagnitudeHistory = mutableListOf<Float>()
    private var gyroMagnitudeHistory = mutableListOf<Float>()
    private val maxHistorySize = 30 // Manter histórico dos últimos 30 eventos

    // Callback de Localização
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                lastLocation = location
                checkVehicleMovement()
            }
        }
    }

    fun startDetection() {
        // Registrar listeners dos sensores com frequência mais alta para melhor detecção
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        linearAcceleration?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        // Iniciar atualização de localização se a permissão for concedida
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(3000)
                .build()

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    fun stopDetection() {
        sensorManager.unregisterListener(this)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _isMovingInVehicle.value = false
    }

    // Lógica de detecção de movimento de veículo com múltiplos critérios
    private fun checkVehicleMovement() {
        val speedMps = lastLocation?.speed ?: 0f
        val speedKmh = speedMps * 3.6

        // Critério 1: Velocidade acima do limiar (> 8 km/h)
        val isSpeeding = speedKmh >= Constants.VEHICLE_SPEED_THRESHOLD_KMH

        // Critério 2: Análise de vibração/aceleração lateral (padrão típico de veículo)
        val hasVehicleVibrationPattern = analyzeVibrationPattern()

        // Critério 3: Movimento contínuo (não parado)
        val isContinuousMovement = speedKmh > 0.5f // Pelo menos um pequeno movimento

        // Combinação de critérios: velocidade + padrão de vibração + movimento contínuo
        val isInVehicle = isSpeeding && (hasVehicleVibrationPattern || isContinuousMovement)

        if (isInVehicle) {
            if (vehicleDetectionStartTime == 0L) {
                vehicleDetectionStartTime = System.currentTimeMillis()
            }
            val duration = System.currentTimeMillis() - vehicleDetectionStartTime
            if (duration >= Constants.VEHICLE_DETECTION_DURATION_SECONDS * 1000) {
                _isMovingInVehicle.value = true
                lastVehicleDetectionTime = System.currentTimeMillis()
            }
        } else {
            // Se não atender aos critérios por mais de 10 segundos, desativar
            if (System.currentTimeMillis() - lastVehicleDetectionTime > 10000) {
                vehicleDetectionStartTime = 0L
                _isMovingInVehicle.value = false
            }
        }
    }

    // Análise de padrão de vibração típico de veículo
    private fun analyzeVibrationPattern(): Boolean {
        if (accelerationMagnitudeHistory.isEmpty()) return false

        // Calcular a variância da aceleração (padrão de vibração)
        val mean = accelerationMagnitudeHistory.average()
        val variance = accelerationMagnitudeHistory.map { (it - mean) * (it - mean) }.average()
        val standardDeviation = sqrt(variance)

        // Padrão típico de veículo: variância significativa (vibração) mas não extrema
        // Valores empíricos: SD entre 0.5 e 5.0 indica movimento de veículo
        return standardDeviation in 0.5f..5.0f
    }

    // Implementação do SensorEventListener
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)

                // Manter histórico de magnitudes de aceleração
                accelerationMagnitudeHistory.add(magnitude)
                if (accelerationMagnitudeHistory.size > maxHistorySize) {
                    accelerationMagnitudeHistory.removeAt(0)
                }

                // Atualizar o estado do fluxo de dados
                _accelerometerData.value = floatArrayOf(x, y, z)
            }
            Sensor.TYPE_GYROSCOPE -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)

                // Manter histórico de magnitudes de giroscópio
                gyroMagnitudeHistory.add(magnitude)
                if (gyroMagnitudeHistory.size > maxHistorySize) {
                    gyroMagnitudeHistory.removeAt(0)
                }

                // Atualizar o estado do fluxo de dados
                _gyroscopeData.value = floatArrayOf(x, y, z)
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                // Usar aceleração linear para movimento sem gravidade
                // Já processado no acelerômetro
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não é necessário implementar para este caso
    }
}
