package com.example.firstlab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder
import org.mariuszgromada.math.mxparser.Expression
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private var inputString = ""
    private var isDegreeMode = true
    private val historyList = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)

        val buttons = arrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
            R.id.button_point, R.id.button_plus, R.id.button_minus, R.id.button_myltiply,
            R.id.button_divide, R.id.button_pow, R.id.button_root, R.id.button_log,
            R.id.button_left_bracket, R.id.button_right_bracket
        )

        val buttonHistory = findViewById<Button>(R.id.button_history)
        buttonHistory.setOnClickListener { openHistoryActivity(it) }

        val buttonAngleMode = findViewById<Button>(R.id.button_angle_mode)
        buttonAngleMode.setOnClickListener {
            isDegreeMode = !isDegreeMode
            buttonAngleMode.text = if (isDegreeMode) "deg" else "rad"
        }

        for (buttonId in buttons) {
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener { onButtonClick(button) }
        }

        val newFunctionButtons = arrayOf(
            R.id.button_log, R.id.button_sin, R.id.button_cos, R.id.button_tan
        )

        for (buttonId in newFunctionButtons) {
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener { onFunctionButtonClick(button) }
        }

        findViewById<Button>(R.id.button_full_clean).setOnClickListener {
            inputString = ""
            updateResult()
        }

        findViewById<Button>(R.id.button_erase).setOnClickListener {
            if (inputString.isNotEmpty()) {
                inputString = inputString.substring(0, inputString.length - 1)
                updateResult()
            }
        }

        findViewById<Button>(R.id.button_equals).setOnClickListener {
            calculateResult()
        }
    }

    private fun onButtonClick(button: Button) {
        val buttonText = when (button.id) {
            R.id.button_root -> "sqrt("
            else -> button.text.toString()
        }
        inputString += buttonText
        updateResult()
    }
    fun openHistoryActivity(view: View) {
        val historyIntent = Intent(this, HistoryActivity::class.java)
        historyIntent.putStringArrayListExtra("historyList", ArrayList(historyList))
        startActivity(historyIntent)
    }

    private fun onFunctionButtonClick(button: Button) {
        val functionText = when (button.id) {
            R.id.button_log -> "log10("
            R.id.button_sin, R.id.button_cos, R.id.button_tan -> {
                if (!isDegreeMode) {
                    "${button.text}("
                } else {
                    "${button.text}(π/180.0*"
                }
            }
            else -> button.text.toString() + "("
        }
        inputString += functionText
        updateResult()
    }

    private fun updateResult() {
        tvResult.text = inputString
    }

    private fun calculateResult() {
        try {
            val result = ExpressionBuilder(inputString).build().evaluate()
            val epsilon = 1e-10

            val historyLine = when {
                Math.abs(result) > 1e16 -> "∞"
                Math.abs(result - result.toInt()) < epsilon -> String.format("%.0f", result)
                result.isNaN() -> "Некорректный ввод"
                else -> result.toString()
            }

            // Добавляем строку в массив истории
            historyList.add("$inputString = $historyLine")

            // Обновляем строку результата
            inputString = historyLine
        } catch (e: ArithmeticException) {
            inputString = "Ошибка"
        } catch (e: Exception) {
            inputString = "Ошибка"
        }
        updateResult()
    }
}