package com.example.firstlab

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class LevelActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private lateinit var btnChangeAccuracy: Button
    private var currentSensorDelay = SensorManager.SENSOR_DELAY_NORMAL
    private lateinit var tvAngle: TextView
    private var lastUpdateTime: Long = 0
    private var currentAngle: Float = 0f
    private lateinit var tvAccuracyMode: TextView
    private lateinit var popupWindow: PopupWindow
    private var isPopupWindowOpen = false
    private lateinit var btnClose: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_layout)
        btnClose = findViewById(R.id.btnClose)

        tvAngle = findViewById(R.id.tvAngle)
        tvAccuracyMode = findViewById(R.id.tvAccuracyMode)
        btnChangeAccuracy = findViewById(R.id.btnChangeAccuracy)
        btnChangeAccuracy.setOnClickListener {
            toggleSensorAccuracy()
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val btnInfo: ImageButton = findViewById(R.id.btnInfo)
        btnInfo.setOnClickListener {
            showInfoPopup()
        }

        btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                currentSensorDelay
            )
        }
        magnetometer?.also { magnetometer ->
            sensorManager.registerListener(
                this,
                magnetometer,
                currentSensorDelay
            )
        }
    }

    private fun showInfoPopup() {
        if (isPopupWindowOpen) {
            return
        }

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.info_popup, null)

        val btnClose: ImageButton = popupView.findViewById(R.id.btnClose)
        btnClose.setOnClickListener {
            popupWindow.dismiss()
        }

        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = (resources.displayMetrics.heightPixels * 1 / 2)
        val focusable = true
        popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.animationStyle = R.style.PopupAnimation

        popupWindow.showAtLocation(popupView, Gravity.TOP, 0, 0)

        isPopupWindowOpen = true

        popupWindow.setOnDismissListener {
            isPopupWindowOpen = false
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }
        updateOrientationAngles()
    }

    private fun toggleSensorAccuracy() {
        when (currentSensorDelay) {
            SensorManager.SENSOR_DELAY_NORMAL -> {
                currentSensorDelay = SensorManager.SENSOR_DELAY_UI
                showToast("Accuracy changed to UI")
            }
            SensorManager.SENSOR_DELAY_UI -> {
                currentSensorDelay = SensorManager.SENSOR_DELAY_GAME
                showToast("Accuracy changed to GAME")
            }
            SensorManager.SENSOR_DELAY_GAME -> {
                currentSensorDelay = SensorManager.SENSOR_DELAY_NORMAL
                showToast("Accuracy changed to NORMAL")
            }
        }

        updateAccuracyModeTextView()

        onResume()
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

        if (Math.abs(degrees - currentAngle) < 5) {
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
