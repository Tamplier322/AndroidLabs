package com.example.firstlab

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {
    private lateinit var tvResult: TextView
    private var currentInput: StringBuilder = StringBuilder()
    private var currentOperator: String = ""
    private var operand1: Double = 0.0
    private var currentOperand: Double? = null

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
            operand1 = currentInput.toString().toDouble()
            currentInput.clear()
            currentOperator = operator
        }
    }

    private fun calculateResult() {
        if (currentInput.isNotEmpty() && currentOperator.isNotEmpty()) {
            val operand2 = currentInput.toString().toDouble()
            when (currentOperator) {
                "+" -> operand1 += operand2
                "-" -> operand1 -= operand2
                "*" -> operand1 *= operand2
                "/" -> operand1 /= operand2
            }

            if (operand1 % 1 == 0.0) {
                currentInput.clear()
                currentInput.append(operand1.toInt())
            } else {
                currentInput.clear()
                currentInput.append(operand1)
            }

            currentOperator = ""
            currentOperand = operand2
            tvResult.text = currentInput.toString()
        }
    }

    private fun eraseText() {
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            tvResult.text = currentInput.toString()
        }
    }
}

