package com.llucs.motioncues

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sqrt

class SensorDetector(private val context: Context) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _isMovingInVehicle = MutableStateFlow(false)
    val isMovingInVehicle: StateFlow<Boolean> = _isMovingInVehicle

    private val _accelerometerData = MutableStateFlow(FloatArray(3))
    private val _gyroscopeData = MutableStateFlow(FloatArray(3))

    private var movingStartTime: Long = 0L
    private var lastUpdateTime: Long = 0L

    private val accelHistory = ArrayDeque<Float>(30)
    private val gyroHistory = ArrayDeque<Float>(30)

    fun isGyroAvailable(): Boolean = gyroscope != null
    fun isAccelerometerAvailable(): Boolean = accelerometer != null

    fun getSensorData(): FloatArray {
        val accel = _accelerometerData.value
        val gyro = _gyroscopeData.value
        return floatArrayOf(accel[0], accel[1], gyro[2])
    }

    fun startDetection() {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        gyroscope?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    fun stopDetection() {
        sensorManager.unregisterListener(this)
        _isMovingInVehicle.value = false
        movingStartTime = 0L
        lastUpdateTime = 0L
        accelHistory.clear()
        gyroHistory.clear()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                _accelerometerData.value = floatArrayOf(x, y, z)

                val magnitude = sqrt(x * x + y * y + z * z)
                pushHistory(accelHistory, magnitude)
            }

            Sensor.TYPE_GYROSCOPE -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                _gyroscopeData.value = floatArrayOf(x, y, z)

                val magnitude = sqrt(x * x + y * y + z * z)
                pushHistory(gyroHistory, magnitude)
            }
        }

        val now = System.currentTimeMillis()
        if (now - lastUpdateTime < 250) return
        lastUpdateTime = now

        val moving = estimateVehicleMovement()
        if (moving) {
            if (movingStartTime == 0L) movingStartTime = now
            val durationMs = now - movingStartTime
            _isMovingInVehicle.value = durationMs >= (Constants.VEHICLE_DETECTION_DURATION_SECONDS * 1000L)
        } else {
            movingStartTime = 0L
            _isMovingInVehicle.value = false
        }
    }

    private fun estimateVehicleMovement(): Boolean {
        if (accelHistory.isEmpty() && gyroHistory.isEmpty()) return false

        val accelStd = standardDeviation(accelHistory)
        val gyroStd = standardDeviation(gyroHistory)

        val accelMoving = accelStd in 0.6..6.0
        val gyroMoving = gyroStd in 0.2..5.0

        return accelMoving || gyroMoving
    }

    private fun standardDeviation(values: ArrayDeque<Float>): Double {
        if (values.size < 5) return 0.0
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        return sqrt(variance)
    }

    private fun pushHistory(deque: ArrayDeque<Float>, v: Float) {
        if (deque.size >= 30) deque.removeFirst()
        deque.addLast(v)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}