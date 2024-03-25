    package com.example.firstlab.Activitis

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.firstlab.Domains.ActivityIntents.ActivityUtils
import com.example.firstlab.Domains.ActivityIntents.InstanceStateUtils
import com.example.firstlab.Domains.Handlers.ButtonClickListener
import com.example.firstlab.Domains.Handlers.ResultHandler
import com.example.firstlab.Domains.PushNotificationManager.PushNotificationManager
import com.example.firstlab.Domains.Utils.Utils
import com.example.firstlab.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.example.firstlab.Domains.FirebaseFiles.FirestoreUtils
import com.example.firstlab.Domains.FirebaseFiles.FirestoreManager
import com.example.firstlab.Domains.FirebaseFiles.applyTheme
import com.example.firstlab.Domains.FirebaseFiles.loadTheme
import com.example.firstlab.Domains.FirebaseFiles.saveTheme
import com.example.firstlab.Domains.Utils.showThemeSelectionDialog

const val THEME_PREFERENCES = "ThemePrefs"
const val SELECTED_THEME = "SelectedTheme"

class MainActivity : AppCompatActivity() {

    lateinit var tvResult: TextView
    lateinit var utils: Utils
    lateinit var resultHandler: ResultHandler
    lateinit var activityUtils: ActivityUtils
    lateinit var instanceStateUtils: InstanceStateUtils
    private val db = Firebase.firestore

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
    private lateinit var firestoreUtils: FirestoreUtils
    private lateinit var firestoreManager: FirestoreManager

    val THEME_DEFAULT = 1
    var currentTheme: Int = THEME_DEFAULT
    var isThemeLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)

        firestoreManager = FirestoreManager(this)
        firestoreUtils = FirestoreUtils(this)

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
            } else {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            }
        })

        if (!isThemeLoaded) {
            firestoreManager.getCurrentThemeFromFirestore { theme ->
                changeTheme(theme)
            }
            isThemeLoaded = true
        }

        firestoreUtils.loadHistoryFromFirestore(historyList)
        //firestoreUtils.loadLastResultFromFirestore(tvResult)
        currentTheme = loadTheme(this)
        applyTheme(this, currentTheme)

        tvResult = findViewById(R.id.tvResult)
        utils = Utils(this)
        resultHandler = ResultHandler(this, utils)
        activityUtils = ActivityUtils(this)
        instanceStateUtils = InstanceStateUtils(this)
        val buttonClickListener = ButtonClickListener(this, resultHandler)
        val buttonEqual = findViewById<Button>(R.id.button_equals)
        val function = intent.getStringExtra("function")
        val angle = intent.getFloatExtra("angle", 0f)
        val themesRef = db.collection("theme")

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

        val buttonTheme = findViewById<Button>(R.id.button_theme)
        buttonTheme.setOnClickListener {
            showThemeSelectionDialog(this) { theme ->
                changeTheme(theme)
            }
        }


        val buttonLevel = findViewById<Button>(R.id.button_level)
        buttonLevel.setOnClickListener {
            activityUtils.openLevelActivity()
        }

        if (function != null && angle != null) {
            val notificationManager = PushNotificationManager(this)
            val notificationMessage = "Выбранная функция: $function, Угол: $angle°"
            notificationManager.sendLevelNotification(notificationMessage)
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
        val lastResultString = tvResult.text.toString()
        val lastResultInt = lastResultString.toIntOrNull()
        if (lastResultInt != null) {
            if (!historyList.isNullOrEmpty()) {
                firestoreUtils.saveHistoryAndLastResultToFirestore(historyList, lastResultInt)
            }
        } else {
            Log.e("YourActivity", "Failed to convert last result to integer")
        }
        super.onSaveInstanceState(outState)
    }


    private fun changeTheme(theme: Int) {
        if (theme != currentTheme) {
            currentTheme = theme
            saveTheme(this, theme)
            firestoreManager.saveCurrentThemeToFirestore(theme)
            recreate()
        }
    }

}
