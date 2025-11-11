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

interface SensorDetector {
    val isMovingInVehicle: StateFlow<Boolean>
    fun startDetection()
    fun stopDetection()
    fun isGyroAvailable(): Boolean
    fun isAccelerometerAvailable(): Boolean
    fun getSensorData(): FloatArray
}

class SensorDetectorImpl(private val context: Context) : SensorDetector, SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // Sensores
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val linearAcceleration: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    // Estados
    private val _isMovingInVehicle = MutableStateFlow(false)
    val isMovingInVehicle: StateFlow<Boolean> = _isMovingInVehicle

    // Dados dos sensores
    private val _accelerometerData = MutableStateFlow(FloatArray(3))
    private val _gyroscopeData = MutableStateFlow(FloatArray(3))

    private var lastLocation: Location? = null
    private var vehicleDetectionStartTime: Long = 0
    private var lastVehicleDetectionTime: Long = 0

    private var accelerationMagnitudeHistory = mutableListOf<Float>()
    private var gyroMagnitudeHistory = mutableListOf<Float>()
    private val maxHistorySize = 30

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                lastLocation = location
                checkVehicleMovement()
            }
        }
    }

    // ======= Métodos públicos para o DotOverlayView =======
    fun isGyroAvailable(): Boolean = gyroscope != null
    fun isAccelerometerAvailable(): Boolean = accelerometer != null
    fun getSensorData(): FloatArray {
        val accel = _accelerometerData.value
        val gyro = _gyroscopeData.value
        // Retorna X/Y do acelerômetro e Z do giroscópio
        return floatArrayOf(accel[0], accel[1], gyro[2])
    }

    // ======= Iniciar e parar detecção =======
    fun startDetection() {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        gyroscope?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        linearAcceleration?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }

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

    // ======= Lógica de detecção de veículo =======
    private fun checkVehicleMovement() {
        val speedMps = lastLocation?.speed ?: 0f
        val speedKmh = speedMps * 3.6

        val isSpeeding = speedKmh >= Constants.VEHICLE_SPEED_THRESHOLD_KMH
        val hasVehicleVibrationPattern = analyzeVibrationPattern()
        val isContinuousMovement = speedKmh > 0.5f

        val isInVehicle = isSpeeding && (hasVehicleVibrationPattern || isContinuousMovement)

        if (isInVehicle) {
            if (vehicleDetectionStartTime == 0L) vehicleDetectionStartTime = System.currentTimeMillis()
            val duration = System.currentTimeMillis() - vehicleDetectionStartTime
            if (duration >= Constants.VEHICLE_DETECTION_DURATION_SECONDS * 1000) {
                _isMovingInVehicle.value = true
                lastVehicleDetectionTime = System.currentTimeMillis()
            }
        } else {
            if (System.currentTimeMillis() - lastVehicleDetectionTime > 10000) {
                vehicleDetectionStartTime = 0L
                _isMovingInVehicle.value = false
            }
        }
    }

    private fun analyzeVibrationPattern(): Boolean {
        if (accelerationMagnitudeHistory.isEmpty()) return false
        val mean = accelerationMagnitudeHistory.average()
        val variance = accelerationMagnitudeHistory.map { (it - mean) * (it - mean) }.average()
        val standardDeviation = sqrt(variance)
        return standardDeviation in 0.5f..5.0f
    }

    // ======= SensorEventListener =======
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)

                accelerationMagnitudeHistory.add(magnitude)
                if (accelerationMagnitudeHistory.size > maxHistorySize) accelerationMagnitudeHistory.removeAt(0)
                _accelerometerData.value = floatArrayOf(x, y, z)
            }
            Sensor.TYPE_GYROSCOPE -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)

                gyroMagnitudeHistory.add(magnitude)
                if (gyroMagnitudeHistory.size > maxHistorySize) gyroMagnitudeHistory.removeAt(0)
                _gyroscopeData.value = floatArrayOf(x, y, z)
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                // Já processado no acelerômetro
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}