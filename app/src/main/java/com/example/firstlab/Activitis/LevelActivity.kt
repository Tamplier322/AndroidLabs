package com.example.firstlab.Activitis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.firstlab.Domains.ActivityIntents.InstanceStateUtils
import com.example.firstlab.Domains.SensorManagers.LevelSensor
import com.example.firstlab.Domains.Utils.LevelPopup
import com.example.firstlab.R
import com.example.firstlab.Domains.Utils.DialogHelper

class LevelActivity : AppCompatActivity() {

    private lateinit var sensorManager: LevelSensor
    private lateinit var levelPopup: LevelPopup
    private lateinit var tvAngle: TextView
    private lateinit var tvAccuracyMode: TextView
    private lateinit var btnClose: ImageButton
    private var savedAngle: Float = 0f
    private val dialogHelper: DialogHelper by lazy { DialogHelper(this) }

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

        val btnAddToCalc: Button = findViewById(R.id.btnAddToCalc)
        btnAddToCalc.setOnClickListener {
            savedAngle = sensorManager.currentAngle
            dialogHelper.showFunctionSelectionDialog(savedAngle)
        }

        savedInstanceState?.let {
            val instanceStateUtils = InstanceStateUtils(MainActivity())
            instanceStateUtils.onRestoreInstanceState(it)
        }

        btnClose.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            finish()
        }

        sensorManager.tvAngle = tvAngle
        sensorManager.tvAccuracyMode = tvAccuracyMode
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val instanceStateUtils = InstanceStateUtils(MainActivity())
        instanceStateUtils.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
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
