// LevelSensor.kt
package com.example.firstlab.Domains.SensorManagers

import android.animation.ValueAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView
import android.widget.Toast
import java.util.*

class LevelSensor(private val context: Context) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    lateinit var tvAngle: TextView
    lateinit var tvAccuracyMode: TextView
    var currentAngle: Float = 0f
    var currentSensorDelay = SensorManager.SENSOR_DELAY_NORMAL

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun registerSensors() {
        accelerometer?.also { sensorManager.registerListener(this, it, currentSensorDelay) }
        magnetometer?.also { sensorManager.registerListener(this, it, currentSensorDelay) }
    }

    fun unregisterSensors() {
        sensorManager.unregisterListener(this)
    }

    fun toggleSensorAccuracy() {
        currentSensorDelay = when (currentSensorDelay) {
            SensorManager.SENSOR_DELAY_NORMAL -> SensorManager.SENSOR_DELAY_UI
            SensorManager.SENSOR_DELAY_UI -> SensorManager.SENSOR_DELAY_GAME
            SensorManager.SENSOR_DELAY_GAME -> SensorManager.SENSOR_DELAY_NORMAL
            else -> SensorManager.SENSOR_DELAY_NORMAL
        }

        updateAccuracyModeTextView()

        registerSensors()

        val accuracyModeText = when (currentSensorDelay) {
            SensorManager.SENSOR_DELAY_NORMAL -> "NORMAL"
            SensorManager.SENSOR_DELAY_UI -> "UI"
            SensorManager.SENSOR_DELAY_GAME -> "GAME"
            else -> "UNKNOWN"
        }
        showToast("Accuracy changed to $accuracyModeText")
    }

    private fun updateAccuracyModeTextView() {
        val accuracyModeText = when (currentSensorDelay) {
            SensorManager.SENSOR_DELAY_NORMAL -> "NORMAL"
            SensorManager.SENSOR_DELAY_UI -> "UI"
            SensorManager.SENSOR_DELAY_GAME -> "GAME"
            else -> "UNKNOWN"
        }
        tvAccuracyMode.text = "Текущий режим точности: $accuracyModeText"
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }
        updateOrientationAngles()
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        val degrees = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

        if (Math.abs(degrees - currentAngle) < 45) {
            val animator = ValueAnimator.ofFloat(currentAngle, degrees)
            animator.duration = 300
            animator.addUpdateListener { animation ->
                val newAngle = animation.animatedValue as Float
                val formattedAngle = String.format(Locale.getDefault(), "%.2f", newAngle)
                tvAngle.text = "Угол: $formattedAngle°"
                currentAngle = newAngle
            }
            animator.start()
        } else {
            val formattedAngle = String.format(Locale.getDefault(), "%.2f", degrees)
            tvAngle.text = "Угол: $formattedAngle°"
            currentAngle = degrees
        }
    }
}
