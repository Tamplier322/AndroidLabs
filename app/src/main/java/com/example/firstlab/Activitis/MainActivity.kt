package com.example.firstlab.Activitis

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.firstlab.Domains.ActivityIntents.ActivityUtils
import com.example.firstlab.Domains.ActivityIntents.InstanceStateUtils
import com.example.firstlab.Domains.Handlers.ButtonClickListener
import com.example.firstlab.Domains.Handlers.ResultHandler
import com.example.firstlab.Domains.Utils.Utils
import com.example.firstlab.R

class MainActivity : AppCompatActivity() {

    lateinit var tvResult: TextView
    lateinit var utils: Utils
    lateinit var resultHandler: ResultHandler
    lateinit var activityUtils: ActivityUtils
    lateinit var instanceStateUtils: InstanceStateUtils

    var inputString = ""
    var isDegreeMode = true
    var lastInput = ""
    var isMaxDigitsExceeded = false
    var currentNumberLength = 0
    var lastNumberIndex = -1
    var lastOperatorIndex = -1
    val MAX_CHARACTERS = 15
    val INPUT_STRING_KEY = "inputString"
    val HISTORY_LIST_KEY = "historyList"
    val ANGLE_MODE_KEY = "angleMode"
    val maxCharacters = 15
    val textSizeOriginal = 36f
    val textSizeSmall = 28f
    val historyList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)
        utils = Utils(this)
        resultHandler = ResultHandler(this, utils)
        activityUtils = ActivityUtils(this)
        instanceStateUtils = InstanceStateUtils(this)
        val buttonClickListener = ButtonClickListener(this, resultHandler)
        val buttonEqual = findViewById<Button>(R.id.button_equals)
        val function = intent.getStringExtra("function")
        val angle = intent.getFloatExtra("angle", 0f)

        if (function != null) {
            val functionText = when (function) {
                "sin" -> "sin(π/180.0*${angle})"
                "cos" -> "cos(π/180.0*${angle})"
                "tan" -> "tan(π/180.0*${angle})"
                else -> ""
            }
            inputString += functionText
        }

        val buttons = arrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
            R.id.button_point, R.id.button_plus, R.id.button_minus, R.id.button_myltiply,
            R.id.button_divide, R.id.button_pow, R.id.button_root, R.id.button_log,
            R.id.button_left_bracket, R.id.button_right_bracket
        )

        val buttonLevel = findViewById<Button>(R.id.button_level)
        buttonLevel.setOnClickListener {
            activityUtils.openLevelActivity()
        }

        val buttonHistory = findViewById<Button>(R.id.button_history)
        buttonHistory.setOnClickListener { activityUtils.openHistoryActivity(ArrayList(historyList)) }

        val buttonAngleMode = findViewById<Button>(R.id.button_angle_mode)
        buttonAngleMode.setOnClickListener {
            isDegreeMode = !isDegreeMode
            utils.updateAngleModeButton(isDegreeMode)
        }

        val newFunctionButtons = arrayOf(
            R.id.button_log, R.id.button_sin, R.id.button_cos, R.id.button_tan
        )

        for (buttonId in buttons) {
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener { buttonClickListener.onButtonClick(button) }
        }

        for (buttonId in newFunctionButtons) {
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener { buttonClickListener.onFunctionButtonClick(button) }
        }

        findViewById<Button>(R.id.button_full_clean).setOnClickListener {
            tvResult.text = "0"
            inputString = ""
            isMaxDigitsExceeded = false
            currentNumberLength = 0
        }

        buttonEqual.setOnClickListener {
            if (inputString.isNotEmpty()) {
                resultHandler.calculateResult()
            }
        }


        findViewById<Button>(R.id.button_erase).setOnClickListener {
            if (inputString.isNotEmpty()) {
                inputString = inputString.substring(0, inputString.length - 1)
                resultHandler.updateResult()
            }
        }

        findViewById<Button>(R.id.button_equals).setOnClickListener {
            resultHandler.calculateResult()
        }

        savedInstanceState?.let {
            instanceStateUtils.onRestoreInstanceState(savedInstanceState)
        }


        isDegreeMode = savedInstanceState?.getBoolean(ANGLE_MODE_KEY, true) ?: true
        utils.updateAngleModeButton(isDegreeMode)
        resultHandler.updateResult()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        instanceStateUtils.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
