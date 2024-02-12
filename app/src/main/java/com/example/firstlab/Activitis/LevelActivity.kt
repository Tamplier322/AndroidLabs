package com.example.firstlab.Activitis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.firstlab.Domains.SensorManagers.LevelSensor
import com.example.firstlab.Domains.Utils.LevelPopup
import com.example.firstlab.R

class LevelActivity : AppCompatActivity() {

    private lateinit var sensorManager: LevelSensor
    private lateinit var levelPopup: LevelPopup
    private lateinit var tvAngle: TextView
    private lateinit var tvAccuracyMode: TextView
    private lateinit var btnClose: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_layout)
        btnClose = findViewById(R.id.btnClose)

        tvAngle = findViewById(R.id.tvAngle)
        tvAccuracyMode = findViewById(R.id.tvAccuracyMode)
        val btnChangeAccuracy: Button = findViewById(R.id.btnChangeAccuracy)

        sensorManager = LevelSensor(this)
        levelPopup = LevelPopup(this)

        btnChangeAccuracy.setOnClickListener {
            sensorManager.toggleSensorAccuracy()
        }

        val btnInfo: ImageButton = findViewById(R.id.btnInfo)
        btnInfo.setOnClickListener {
            levelPopup.showInfoPopup()
        }

        btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        sensorManager.tvAngle = tvAngle
        sensorManager.tvAccuracyMode = tvAccuracyMode
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerSensors()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterSensors()
    }
}
