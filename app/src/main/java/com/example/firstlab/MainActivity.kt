package com.example.firstlab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private lateinit var tvResult: TextView
    private var currentInput: StringBuilder = StringBuilder()
    private var currentOperator: String = ""
    private var operand1: Double = 0.0
    private var currentOperand: Double? = null
    private val history: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)

        val digitButtons = mapOf(
            R.id.button0 to "0",
            R.id.button1 to "1",
            R.id.button2 to "2",
            R.id.button3 to "3",
            R.id.button4 to "4",
            R.id.button5 to "5",
            R.id.button6 to "6",
            R.id.button7 to "7",
            R.id.button8 to "8",
            R.id.button9 to "9"
        )

        for ((buttonId, digit) in digitButtons) {
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener {
                appendToTextView(digit)
            }
        }

        val operatorButtons = mapOf(
            R.id.button_plus to "+",
            R.id.button_minus to "-",
            R.id.button_myltiply to "*",
            R.id.button_divide to "/"
        )

        for ((buttonId, operator) in operatorButtons) {
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener {
                handleOperator(operator)
            }
        }

        val equalsButton = findViewById<Button>(R.id.button_equals)
        equalsButton.setOnClickListener {
            calculateResult()
        }

        val eraseButton = findViewById<Button>(R.id.button_erase)
        eraseButton.setOnClickListener {
            eraseText()
        }

        val clearButton = findViewById<Button>(R.id.button_full_clean)
        clearButton.setOnClickListener {
            clearAll()
        }

        val pointButton = findViewById<Button>(R.id.button_point)
        pointButton.setOnClickListener {
            appendPoint()
        }

        val percentButton = findViewById<Button>(R.id.button_percent)
        percentButton.setOnClickListener {
            handlePercent()
        }

        val rootButton = findViewById<Button>(R.id.button_root)
        rootButton.setOnClickListener {
            handleRoot()
        }

        val changeSignButton = findViewById<Button>(R.id.button_change_sign)
        changeSignButton.setOnClickListener {
            handleChangeSign()
        }

        val piButton = findViewById<Button>(R.id.button_Pi)
        piButton.setOnClickListener {
            handlePi()
        }

        val historyButton = findViewById<Button>(R.id.button_history)
        historyButton.setOnClickListener {
            val historyIntent = Intent(this, HistoryActivity::class.java)
            historyIntent.putExtra("history", generateHistoryString())
            startActivity(historyIntent)
        }
    }

    private fun addToHistory(operation: String) {
        history.add(operation)
    }

    // Генерация строки истории
    private fun generateHistoryString(): String {
        val stringBuilder = StringBuilder("History:\n")
        for ((index, operation) in history.withIndex()) {
            stringBuilder.append("$operation\n")
        }
        return stringBuilder.toString()
    }

    private fun appendToTextView(text: String) {
        currentInput.append(text)
        tvResult.text = currentInput.toString()
    }

    private fun clearAll() {
        currentInput.clear()
        currentOperator = ""
        operand1 = 0.0
        currentOperand = null
        tvResult.text = "0"
    }

    private fun appendPoint() {
        if (!currentInput.contains('.')) {
            if (currentInput.isEmpty()) {
                currentInput.append("0.")
            } else {
                currentInput.append('.')
            }
            tvResult.text = currentInput.toString()
        }
    }

    private fun handleOperator(operator: String) {
        if (currentInput.isNotEmpty()) {
            if (currentOperand != null) {
                calculateResult()
            }
            operand1 = parseInput(currentInput.toString())
            currentInput.clear()
            currentOperator = operator
        }
    }

    private fun parseInput(input: String): Double {
        // Проверяем, содержит ли ввод Pi и обрабатываем соответствующим образом
        return if (input.contains("Pi")) {
            input.replace("Pi", kotlin.math.PI.toString()).toDouble()
        } else {
            input.toDouble()
        }
    }

    private fun calculateResult() {
        if (currentInput.isNotEmpty() && currentOperator.isNotEmpty()) {
            val operand2 = parseInput(currentInput.toString())

            // Добавим проверку деления на ноль
            if (currentOperator == "/" && operand2 == 0.0) {
                tvResult.text = "Делить на ноль нельзя"
                return
            }

            var result: Double? = null

            when (currentOperator) {
                "+" -> result = operand1 + operand2
                "-" -> result = operand1 - operand2
                "*" -> result = operand1 * operand2
                "/" -> {
                    if (operand2 != 0.0) {
                        result = operand1 / operand2
                    }
                }
            }

            if (result != null) {
                currentInput.clear()
                currentInput.append(result.toString()) // Просто добавляем как строку

                // Используем форматирование для строки истории
                val historyEntry = if (currentOperator == "/") {
                    // Отдельно обрабатываем деление, чтобы избежать дробных нулей в выводе
                    String.format("%s %s %.1f = %.1f", operand1, currentOperator, operand2, result)
                } else {
                    String.format("%s %s %s = %s", operand1, currentOperator, operand2, result)
                }

                addToHistory(historyEntry)
            }

            currentOperator = ""
            currentOperand = null
            tvResult.text = currentInput.toString()
        }
    }




    private fun eraseText() {
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            tvResult.text = currentInput.toString()
        }
    }

    private fun handlePercent() {
        if (currentInput.isNotEmpty()) {
            val inputValue = currentInput.toString().toDouble()
            val result = inputValue / 100
            currentInput.clear()
            currentInput.append(result)
            tvResult.text = currentInput.toString()
        }
    }

    private fun handleRoot() {
        if (currentInput.isNotEmpty()) {
            val inputValue = currentInput.toString().toDouble()
            val result = sqrt(inputValue)
            currentInput.clear()
            currentInput.append(result)
            tvResult.text = currentInput.toString()
        }
    }

    private fun handleChangeSign() {
        if (currentInput.isNotEmpty()) {
            val inputValue = currentInput.toString().toDouble()
            val result = -inputValue

            if (result % 1 == 0.0) {
                currentInput.clear()
                currentInput.append(result.toInt())
            } else {
                currentInput.clear()
                currentInput.append(result)
            }

            tvResult.text = currentInput.toString()
        }
    }


    private fun handlePi() {
        val piValue = kotlin.math.PI

        // Check if Pi is already present in the input
        if (!currentInput.contains(piValue.toString())) {
            if (currentInput.isEmpty() || currentInput.toString() == "0") {
                currentInput.clear()
                currentInput.append(piValue)
            } else {
                currentInput.clear()
                currentInput.append(" $piValue")
            }

            tvResult.text = currentInput.toString()
        }
    }
}
