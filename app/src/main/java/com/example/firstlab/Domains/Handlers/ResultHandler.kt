// ResultHandler.kt
package com.example.firstlab.Domains.Handlers

import android.util.TypedValue
import android.view.View
import android.widget.ScrollView
import com.example.firstlab.Activitis.MainActivity
import com.example.firstlab.Domains.PushNotificationManager.PushNotificationManager
import java.math.BigDecimal
import java.math.RoundingMode
import com.example.firstlab.R
import com.example.firstlab.Domains.Utils.Utils
import kotlin.math.absoluteValue

class ResultHandler(private val activity: MainActivity, private val utils: Utils) {

    private val pushNotificationManager = PushNotificationManager(activity)
    var lastResultFromFirestore: String? = null


    fun updateResult() {
        activity.tvResult.text = lastResultFromFirestore ?: ""

        val inputString = activity.inputString
        val maxCharacters = activity.maxCharacters
        val textSizeOriginal = activity.textSizeOriginal
        val textSizeSmall = activity.textSizeSmall

        if (inputString.isEmpty()) {
            activity.tvResult.text = "0"
            return
        }

        if (inputString.length > maxCharacters) {
            val lines = inputString.count { it == '\n' } + 1
            if (lines < 2) {
                activity.tvResult.append("\n$inputString")
                activity.tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSmall)
            } else {
                val currentText = activity.tvResult.text.toString()
                val newText = currentText.substring(currentText.indexOf('\n') + 1) + "\n$inputString"
                activity.tvResult.text = newText
            }
        } else {
            activity.tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeOriginal)
            activity.tvResult.text = inputString
        }

        val scrollView = activity.findViewById<ScrollView>(R.id.scrollView)
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    fun appendResultText(text: String) {
        activity.tvResult.append(text)
    }

    fun calculateResult() {
        var inputString = activity.inputString
        inputString = utils.autoCloseBrackets(inputString)

        try {
            val expression = net.objecthunter.exp4j.ExpressionBuilder(inputString).build()
            val result = expression.evaluate()
            val historyList = activity.historyList

            val historyLine = when {
                result.isInfinite() -> "∞"
                result.isNaN() -> "Некорректный ввод"
                else -> {
                    val formattedResult = when {
                        result.toString().contains('E') || result.toString().contains('e') -> result.toString()
                        else -> {
                            val roundedResult = BigDecimal(result.toString()).setScale(10, RoundingMode.HALF_EVEN)
                            roundedResult.stripTrailingZeros().toPlainString()
                        }
                    }
                    formattedResult
                }
            }

            historyList.add("$inputString = $historyLine")
            activity.inputString = historyLine
            appendResultText("\n")
        } catch (e: ArithmeticException) {
            val errorMessage = "Ошибка: Деление на ноль"
            activity.inputString = errorMessage
            pushNotificationManager.sendErrorNotification(errorMessage)
        } catch (e: IllegalArgumentException) {
            val errorMessage = "Ошибка: Некорректное выражение"
            activity.inputString = errorMessage
            pushNotificationManager.sendErrorNotification(errorMessage)
        } catch (e: Exception) {
            val errorMessage = "Ошибка: ${e.message}"
            activity.inputString = errorMessage
            pushNotificationManager.sendErrorNotification(errorMessage)
        }
        updateResult()

    }
}
