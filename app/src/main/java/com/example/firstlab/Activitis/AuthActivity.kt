package com.example.firstlab.Activitis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.firstlab.R
import com.example.firstlab.databinding.ActivityLoginBinding
import java.util.concurrent.Executor
import android.util.Base64
import android.util.Log
import java.util.Arrays
import javax.crypto.spec.SecretKeySpec
import com.example.firstlab.Domains.Utils.PinEncryptionUtils
import com.example.firstlab.Domains.Utils.appendDigit
import com.example.firstlab.Domains.Utils.biometricAuth
import com.example.firstlab.Domains.Utils.deleteAllPin
import com.example.firstlab.Domains.Utils.deleteDigitPin
import com.example.firstlab.Domains.Utils.login
import com.example.firstlab.Domains.Utils.setPin
import com.example.firstlab.Domains.Utils.showBiometricConfirmationDialog
import com.example.firstlab.Domains.Utils.showPin

private const val SHIFT = 3
private const val KEY_NAME = "pin_key"


class AuthActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var executor: Executor
    lateinit var biometricPrompt: BiometricPrompt
    lateinit var promptInfo: BiometricPrompt.PromptInfo
    lateinit var pinEncryptionUtils: PinEncryptionUtils
    lateinit var pinInputTextView: TextView
    val MAX_PIN_LENGTH = 6
    var storedPin: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pinInputTextView = findViewById(R.id.pinInputTextView)
        executor = ContextCompat.getMainExecutor(this)
        pinEncryptionUtils = PinEncryptionUtils()

        loadStoredPin()
    }

    fun biometricAuth(view: View) {
        biometricAuth(this)
    }

    private fun showBiometricConfirmationDialog(newPin: String) {
        showBiometricConfirmationDialog(this, newPin)
    }

    fun showPin(view: View) {
        showPin(this)
    }

    private fun loadStoredPin() {
        storedPin = pinEncryptionUtils.loadPinFromSecureStorage(this)
    }

    fun appendDigit(view: View) {
        appendDigit(this, pinInputTextView, MAX_PIN_LENGTH, view as TextView)
    }

    fun deleteDigit(view: View) {
        deleteDigitPin(pinInputTextView)
    }

    fun deleteAll(view: View) {
        deleteAllPin(pinInputTextView)
    }

    fun setPin(view: View) {
        setPin(this, pinInputTextView, storedPin, MAX_PIN_LENGTH)
    }

    fun login(view: View) {
        login(this, pinInputTextView, storedPin, MAX_PIN_LENGTH)
    }
}