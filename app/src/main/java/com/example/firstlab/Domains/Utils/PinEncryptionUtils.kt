package com.example.firstlab.Domains.Utils

import android.content.Context
import android.util.Base64
import android.util.Log
import java.util.Arrays
import javax.crypto.spec.SecretKeySpec
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.nio.charset.StandardCharsets

private const val SHIFT = 3
private const val KEY_NAME = "pin_key"

class PinEncryptionUtils {

    private var generatedKey = generatePassKey()

    init {
        Log.d("PinEncryptionUtils", "Generated key: ${generatedKey}")
    }

    fun encryptPin(pin: String): ByteArray {
        val key = generatedKey
        val keyBytes = key.encoded
        val pinBytes = pin.toByteArray(StandardCharsets.UTF_8)
        val encryptedBytes = ByteArray(pinBytes.size)
        for (i in pinBytes.indices) {
            encryptedBytes[i] = (pinBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }
        return encryptedBytes
    }

    fun decryptPin(encryptedPin: ByteArray): String {
        val key = generatedKey
        val keyBytes = key.encoded
        val decryptedBytes = ByteArray(encryptedPin.size)
        for (i in encryptedPin.indices) {
            decryptedBytes[i] = (encryptedPin[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }

    private fun generatePassKey(): SecretKeySpec {
        val keyBytes = ByteArray(16)
        java.util.Arrays.fill(keyBytes, SHIFT.toByte())
        return SecretKeySpec(keyBytes, "AES")
    }

    fun savePinToSecureStorage(context: Context, pin: String) {
        try {
            val encryptedPin = encryptPin(pin)
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val sharedPreferences = EncryptedSharedPreferences.create(
                "SecureStorage",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            sharedPreferences.edit().putString(KEY_NAME, Base64.encodeToString(encryptedPin, Base64.DEFAULT)).apply()
        } catch (e: Exception) {
            Log.e("PinEncryptionUtils", "Error saving PIN to secure storage", e)
        }
    }

    fun loadPinFromSecureStorage(context: Context): String? {
        return try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val sharedPreferences = EncryptedSharedPreferences.create(
                "SecureStorage",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val encryptedPinString = sharedPreferences.getString(KEY_NAME, null)
            if (encryptedPinString != null) {
                val encryptedPin = Base64.decode(encryptedPinString, Base64.DEFAULT)
                decryptPin(encryptedPin)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("PinEncryptionUtils", "Error loading PIN from secure storage", e)
            null
        }
    }
}