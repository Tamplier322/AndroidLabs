package com.example.firstlab.Activitis

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.firstlab.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 2000
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen)
        
        supportActionBar?.hide()

        getCurrentThemeFromFirestore()
    }

    private fun getCurrentThemeFromFirestore() {
        val themesRef = db.collection("theme")
        themesRef.document("7rGDPSlMyggDXGsYwpnu").get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val theme = document.getLong("current_theme")
                    if (theme != null) {
                        saveTheme(theme.toInt())
                        openMainActivity()
                    }
                }
            }
            .addOnFailureListener { exception ->
                openMainActivity()
            }
    }

    private fun saveTheme(theme: Int) {
        val preferences: SharedPreferences = getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt(SELECTED_THEME, theme)
        editor.apply()
    }

    private fun openMainActivity() {
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }, SPLASH_TIME_OUT)
    }
}

