package com.example.firstlab.Activitis

import android.os.Bundle
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
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

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


    private val THEME_PREFERENCES = "ThemePrefs"
    private val SELECTED_THEME = "SelectedTheme"

    private val THEME_DEFAULT = 1
    private val THEME_ONE = 1
    private val THEME_TWO = 2
    private val THEME_THREE = 3
    private val THEME_FOUR = 4
    private var currentTheme: Int = THEME_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadTheme()
        applyTheme()

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

        val buttonTheme = findViewById<Button>(R.id.button_theme)
        buttonTheme.setOnClickListener {
            showThemeSelectionDialog()
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
        super.onSaveInstanceState(outState)
    }

    private fun showThemeSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.theme_selection_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        dialog.window?.attributes?.windowAnimations = R.style.SlideInAnimation

        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        val themes = arrayOf(
            R.id.button_theme_1,
            R.id.button_theme_2,
            R.id.button_theme_3,
            R.id.button_theme_4
        )

        val colorCircles = arrayOf(
            R.id.color_circle_1,
            R.id.color_circle_2,
            R.id.color_circle_3,
            R.id.color_circle_4
        )

        for (i in themes.indices) {
            val themeButton = dialogView.findViewById<Button>(themes[i])
            themeButton.setOnClickListener {
                changeTheme(i + 1)
                dialog.dismiss()
            }

            val colorCircle = dialogView.findViewById<View>(colorCircles[i])
            val colorPrimary = ContextCompat.getColor(this, getColorPrimary(i + 1))
            val backgroundColor = ContextCompat.getColor(this, getBackgroundColor(i + 1))
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(colorPrimary, backgroundColor)
            )
            gradientDrawable.shape = GradientDrawable.OVAL
            colorCircle.background = gradientDrawable
        }

        dialog.show()
    }

    private fun getColorPrimary(theme: Int): Int {
        return when (theme) {
            THEME_ONE -> R.color.colorPrimary1
            THEME_TWO -> R.color.colorPrimary2
            THEME_THREE -> R.color.colorPrimary3
            THEME_FOUR -> R.color.colorPrimary4
            else -> R.color.colorPrimary1 // По умолчанию первая тема
        }
    }

    private fun getBackgroundColor(theme: Int): Int {
        return when (theme) {
            THEME_ONE -> R.color.backgroundColor1
            THEME_TWO -> R.color.backgroundColor2
            THEME_THREE -> R.color.backgroundColor3
            THEME_FOUR -> R.color.backgroundColor4
            else -> R.color.backgroundColor1 // По умолчанию первая тема
        }
    }

    private fun changeTheme(theme: Int) {
        currentTheme = theme
        saveTheme()
        recreate()
    }

    private fun saveTheme() {
        val preferences: SharedPreferences = getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt(SELECTED_THEME, currentTheme)
        editor.apply()
    }

    private fun loadTheme() {
        val preferences: SharedPreferences = getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
        currentTheme = preferences.getInt(SELECTED_THEME, THEME_DEFAULT)
    }

    private fun applyTheme() {
        when (currentTheme) {
            THEME_ONE -> {
                setTheme(R.style.AppTheme1)
                setButtonColors(
                    R.color.colorPrimary1,
                    R.color.colorPrimaryDark1,
                    R.color.colorAccent1,
                    R.color.textColorPrimary1,
                    R.color.backgroundColor1
                )
                ViewCompat.setBackgroundTintList(findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(this, R.color.backgroundColor1)))
                findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColor1))
            }
            THEME_TWO -> {
                setTheme(R.style.AppTheme2)
                setButtonColors(
                    R.color.colorPrimary2,
                    R.color.colorPrimaryDark2,
                    R.color.colorAccent2,
                    R.color.textColorPrimary2,
                    R.color.backgroundColor2
                )
                ViewCompat.setBackgroundTintList(findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(this, R.color.backgroundColor2)))
                findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColor2))
            }
            THEME_THREE -> {
                setTheme(R.style.AppTheme3)
                setButtonColors(
                    R.color.colorPrimary3,
                    R.color.colorPrimaryDark3,
                    R.color.colorAccent3,
                    R.color.textColorPrimary3,
                    R.color.backgroundColor3
                )
                ViewCompat.setBackgroundTintList(findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(this, R.color.backgroundColor3)))
                findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColor3))
            }
            THEME_FOUR -> {
                setTheme(R.style.AppTheme4)
                setButtonColors(
                    R.color.colorPrimary4,
                    R.color.colorPrimaryDark4,
                    R.color.colorAccent4,
                    R.color.textColorPrimary4,
                    R.color.backgroundColor4
                )
                ViewCompat.setBackgroundTintList(findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(this, R.color.backgroundColor4)))
                findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColor4))
            }
            else -> {
                setTheme(R.style.AppTheme1)
                setButtonColors(
                    R.color.colorPrimary1,
                    R.color.colorPrimaryDark1,
                    R.color.colorAccent1,
                    R.color.textColorPrimary1,
                    R.color.backgroundColor1
                )
                ViewCompat.setBackgroundTintList(findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(this, R.color.backgroundColor1)))
                findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColor1))
            }
        }
    }


    private fun setButtonColors(
        primaryColor: Int,
        primaryDarkColor: Int,
        accentColor: Int,
        textColor: Int,
        backgroundColor: Int
    ) {
        val buttons = arrayOf(
            R.id.button_history, R.id.button_angle_mode, R.id.button_level, R.id.button_log,
            R.id.button_sin, R.id.button_cos, R.id.button_tan, R.id.button_pow, R.id.button_root,
            R.id.button_left_bracket, R.id.button_right_bracket, R.id.button_full_clean,
            R.id.button_erase, R.id.button_divide, R.id.button_myltiply, R.id.button_minus,
            R.id.button_plus, R.id.button9, R.id.button8, R.id.button7, R.id.button6, R.id.button5,
            R.id.button4, R.id.button3, R.id.button2, R.id.button1, R.id.button_point, R.id.button0,
            R.id.button_equals, R.id.button_theme
        )

        for (buttonId in buttons) {
            val button = findViewById<Button>(buttonId)
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, primaryColor))
            button.setTextColor(ContextCompat.getColor(this, textColor))
        }

        val tvResult = findViewById<TextView>(R.id.tvResult)
        tvResult.setTextColor(ContextCompat.getColor(this, textColor))
        tvResult.setBackgroundColor(ContextCompat.getColor(this, backgroundColor))
    }
}
