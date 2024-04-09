package com.example.firstlab.Domains.Utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firstlab.Activitis.AuthActivity
import com.example.firstlab.Activitis.SplashScreenActivity
import com.example.firstlab.R
import com.example.firstlab.databinding.ActivityLoginBinding
import com.example.firstlab.Domains.Utils.PinEncryptionUtils

fun setPin(activity: AuthActivity, pinInputTextView: TextView, storedPin: String?, MAX_PIN_LENGTH: Int) {
    val pin = pinInputTextView.text.toString()
    if (pin.length == MAX_PIN_LENGTH) {
        if (storedPin == null) {
            activity.pinEncryptionUtils.savePinToSecureStorage(activity, pin)
            activity.storedPin = pin
            Toast.makeText(activity.applicationContext, "PIN set successfully", Toast.LENGTH_SHORT).show()
        } else {
            showBiometricConfirmationDialog(activity, pin)
        }
    } else {
        Toast.makeText(activity.applicationContext, "PIN must contain $MAX_PIN_LENGTH digits", Toast.LENGTH_SHORT).show()
    }
}

fun login(activity: AuthActivity, pinInputTextView: TextView, storedPin: String?, MAX_PIN_LENGTH: Int) {
    val enteredPin = pinInputTextView.text.toString()
    if (storedPin == null) {
        Toast.makeText(activity.applicationContext, "PIN not set", Toast.LENGTH_SHORT).show()
    } else if (enteredPin.length == MAX_PIN_LENGTH && enteredPin == storedPin) {
        Toast.makeText(activity.applicationContext, "PIN correct. Logging in...", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, SplashScreenActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    } else {
        Toast.makeText(activity.applicationContext, "Incorrect PIN", Toast.LENGTH_SHORT).show()
    }
}

fun appendDigit(activity: AuthActivity, pinInputTextView: TextView, MAX_PIN_LENGTH: Int, view: TextView) {
    if (pinInputTextView.text.length < MAX_PIN_LENGTH) {
        val digit = (view as TextView).text
        pinInputTextView.append(digit)
    }
}

fun deleteDigitPin(pinInputTextView: TextView) {
    val currentPin = pinInputTextView.text.toString()
    if (currentPin.isNotEmpty()) {
        val newPin = currentPin.substring(0, currentPin.length - 1)
        pinInputTextView.text = newPin
    }
}

fun deleteAllPin(pinInputTextView: TextView) {
    pinInputTextView.text = ""
}