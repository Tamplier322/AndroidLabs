package com.example.firstlab.Domains.Utils

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.firstlab.Activitis.MainActivity
import com.example.firstlab.R
import java.util.Locale
import android.view.LayoutInflater
import com.example.firstlab.Domains.PushNotificationManager.PushNotificationManager

class DialogHelper(private val context: Context) {

    private var savedAngle: Float = 0f
    private var functionSelectionDialog: AlertDialog? = null

    fun showFunctionSelectionDialog(savedAngle: Float) {
        functionSelectionDialog?.dismiss()

        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.func_popup, null)


        val btnSin: Button = dialogView.findViewById(R.id.btnSin)
        val btnCos: Button = dialogView.findViewById(R.id.btnCos)
        val btnTan: Button = dialogView.findViewById(R.id.btnTan)

        val tvCurrentAngle: TextView = dialogView.findViewById(R.id.tvCurrentAngle)
        tvCurrentAngle.text = "Текущий угол: ${String.format(Locale.getDefault(), "%.2f", savedAngle)}°"

        btnSin.setOnClickListener {
            navigateToCalculator("sin", savedAngle)
        }

        btnCos.setOnClickListener {
            navigateToCalculator("cos", savedAngle)
        }

        btnTan.setOnClickListener {
            navigateToCalculator("tan", savedAngle)
        }

        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.attributes?.windowAnimations = R.style.PopupAnimation

        functionSelectionDialog = dialog

        dialog.show()
    }

    // В методе DialogHelper.navigateToCalculator добавьте отправку уведомления
    private fun navigateToCalculator(selectedFunction: String, savedAngle: Float) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("function", selectedFunction)
            putExtra("angle", savedAngle)
        }
        context.startActivity(intent)

        val notificationManager = PushNotificationManager(context)
        val notificationMessage = "Выбранная функция: $selectedFunction, Угол: $savedAngle°"
        notificationManager.sendLevelNotification(notificationMessage)
    }
}
