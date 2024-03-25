package com.example.firstlab.Domains.FirebaseFiles

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Build
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.firstlab.Activitis.MainActivity
import com.example.firstlab.R

const val THEME_PREFERENCES = "ThemePrefs"
const val SELECTED_THEME = "SelectedTheme"
val THEME_DEFAULT = 1
val THEME_ONE = 1
val THEME_TWO = 2
val THEME_THREE = 3
val THEME_FOUR = 4
var currentTheme: Int = THEME_DEFAULT

fun applyTheme(activity: MainActivity, currentTheme: Int) {
    when (currentTheme) {
        THEME_ONE -> {
            activity.setTheme(R.style.AppTheme1)
            setButtonColors(activity,
                R.color.colorPrimary1,
                R.color.colorPrimaryDark1,
                R.color.colorAccent1,
                R.color.textColorPrimary1,
                R.color.backgroundColor1
            )
            setStatusBarColor(activity, R.color.colorPrimaryDark1)
            ViewCompat.setBackgroundTintList(activity.findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.backgroundColor1)))
            activity.findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(activity, R.color.backgroundColor1))
        }
        THEME_TWO -> {
            activity.setTheme(R.style.AppTheme2)
            setButtonColors(activity,
                R.color.colorPrimary2,
                R.color.colorPrimaryDark2,
                R.color.colorAccent2,
                R.color.textColorPrimary2,
                R.color.backgroundColor2
            )
            setStatusBarColor(activity, R.color.colorPrimaryDark2)
            ViewCompat.setBackgroundTintList(activity.findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.backgroundColor2)))
            activity.findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(activity, R.color.backgroundColor2))
        }
        THEME_THREE -> {
            activity.setTheme(R.style.AppTheme3)
            setButtonColors(activity,
                R.color.colorPrimary3,
                R.color.colorPrimaryDark3,
                R.color.colorAccent3,
                R.color.textColorPrimary3,
                R.color.backgroundColor3
            )
            setStatusBarColor(activity, R.color.colorPrimaryDark3)
            ViewCompat.setBackgroundTintList(activity.findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.backgroundColor3)))
            activity.findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(activity, R.color.backgroundColor3))
        }
        THEME_FOUR -> {
            activity.setTheme(R.style.AppTheme4)
            setButtonColors(activity,
                R.color.colorPrimary4,
                R.color.colorPrimaryDark4,
                R.color.colorAccent4,
                R.color.textColorPrimary4,
                R.color.backgroundColor4
            )
            setStatusBarColor(activity, R.color.colorPrimaryDark4)
            ViewCompat.setBackgroundTintList(activity.findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.backgroundColor4)))
            activity.findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(activity, R.color.backgroundColor4))
        }
        else -> {
            activity.setTheme(R.style.AppTheme1)
            setButtonColors(activity,
                R.color.colorPrimary1,
                R.color.colorPrimaryDark1,
                R.color.colorAccent1,
                R.color.textColorPrimary1,
                R.color.backgroundColor1
            )
            setStatusBarColor(activity, R.color.colorPrimaryDark1)
            ViewCompat.setBackgroundTintList(activity.findViewById(R.id.gridLayout), ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.backgroundColor1)))
            activity.findViewById<LinearLayout>(R.id.rootLayout).setBackgroundColor(ContextCompat.getColor(activity, R.color.backgroundColor1))
        }
    }
}

fun setStatusBarColor(activity: MainActivity, colorResId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        activity.window.statusBarColor = ContextCompat.getColor(activity, colorResId)
    }
}
fun setButtonColors(activity: MainActivity, primaryColor: Int, primaryDarkColor: Int, accentColor: Int, textColor: Int, backgroundColor: Int) {
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
        val button = activity.findViewById<Button>(buttonId)
        button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, primaryColor))
        button.setTextColor(ContextCompat.getColor(activity, textColor))
    }

    val tvResult = activity.findViewById<TextView>(R.id.tvResult)
    tvResult.setTextColor(ContextCompat.getColor(activity, textColor))
    tvResult.setBackgroundColor(ContextCompat.getColor(activity, backgroundColor))
}

fun saveTheme(activity: MainActivity, currentTheme: Int) {
    val preferences: SharedPreferences = activity.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preferences.edit()
    editor.putInt(SELECTED_THEME, currentTheme)
    editor.apply()
}

fun loadTheme(activity: MainActivity): Int {
    val preferences: SharedPreferences = activity.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
    return preferences.getInt(SELECTED_THEME, THEME_DEFAULT)
}

fun getColorPrimary(context: Context, theme: Int): Int {
    return when (theme) {
        THEME_ONE -> ContextCompat.getColor(context, R.color.colorPrimary1)
        THEME_TWO -> ContextCompat.getColor(context, R.color.colorPrimary2)
        THEME_THREE -> ContextCompat.getColor(context, R.color.colorPrimary3)
        THEME_FOUR -> ContextCompat.getColor(context, R.color.colorPrimary4)
        else -> ContextCompat.getColor(context, R.color.colorPrimary1)
    }
}

fun getBackgroundColor(context: Context, theme: Int): Int {
    return when (theme) {
        THEME_ONE -> ContextCompat.getColor(context, R.color.backgroundColor1)
        THEME_TWO -> ContextCompat.getColor(context, R.color.backgroundColor2)
        THEME_THREE -> ContextCompat.getColor(context, R.color.backgroundColor3)
        THEME_FOUR -> ContextCompat.getColor(context, R.color.backgroundColor4)
        else -> ContextCompat.getColor(context, R.color.backgroundColor1)
    }
}