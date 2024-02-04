package com.example.firstlab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder
import android.util.TypedValue
import android.widget.ScrollView

class MainActivity : AppCompatActivity() {
    private lateinit var tvResult: TextView
    private var inputString = ""
    private var isDegreeMode = true
    private val historyList = mutableListOf<String>()
    private var lastInput = ""
    val MAX_CHARACTERS = 15
    private var isMaxDigitsExceeded = false
    private var currentNumberLength = 0

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
            buttonAngleMode.text = if (isDegreeMode) "Deg" else "Rad"
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
            isMaxDigitsExceeded = false
            currentNumberLength = 0
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
        if (isMaxDigitsReached() && !isAddingDigitsAllowed(button)) {
            return
        }

        val buttonText = when (button.id) {
            R.id.button_root -> "sqrt("
            R.id.button_point -> handlePointButton()
            R.id.button_plus, R.id.button_minus, R.id.button_myltiply, R.id.button_divide -> handleOperatorButton(button)
            R.id.button_left_bracket -> handleLeftBracketButton()
            R.id.button_right_bracket -> handleRightBracketButton()
            else -> button.text.toString()
        }

        if (buttonText == "^" && !canAddPowerSymbol()) {
            return
        }

        if (buttonText[0].isDigit() || buttonText == ".") {
            currentNumberLength++
            if (currentNumberLength > MAX_CHARACTERS) {
                return
            }
        } else {
            currentNumberLength = 0
        }

        if (inputString == "0" && buttonText[0].isDigit()) {
            inputString = buttonText
        } else if (inputString.endsWith("0") && buttonText[0].isDigit() && lastInput.isEmpty()) {
            inputString = buttonText
        } else if (inputString.endsWith("0") && buttonText[0].isDigit() && lastInput[0] in "+-*/") {
            inputString = buttonText
        } else {
            inputString += buttonText
            lastInput = buttonText
            updateResult()
        }
    }


    private fun isAddingDigitsAllowed(button: Button): Boolean {
        val currentInput = when (button.id) {
            R.id.button_plus, R.id.button_minus, R.id.button_myltiply,
            R.id.button_divide, R.id.button_pow, R.id.button_root,
            R.id.button_log, R.id.button_left_bracket, R.id.button_right_bracket -> false
            else -> true
        }

        return currentInput || (lastInput.isNotEmpty() && lastInput[0].isDigit() && currentNumberLength < MAX_CHARACTERS)
    }


    private fun isMaxDigitsReached(): Boolean {
        if (isMaxDigitsExceeded) {
            return false
        }

        val parts = inputString.split("[+\\-*/()]".toRegex())
        val isReached = parts.any { it.length > MAX_CHARACTERS }

        // Обновим переменную в зависимости от результата
        isMaxDigitsExceeded = isReached

        return isReached
    }

    private fun canAddPowerSymbol(): Boolean {
        return inputString.isNotEmpty() && inputString.last().isDigit()
    }

    private fun autoCloseBrackets(): String {
        val openBracketsCount = inputString.count { it == '(' }
        val closeBracketsCount = inputString.count { it == ')' }

        return if (openBracketsCount > closeBracketsCount) {
            ")".repeat(openBracketsCount - closeBracketsCount)
        } else {
            ""
        }
    }

    private fun handlePointButton(): String {
        val parts = inputString.split("[+\\-*/]".toRegex())
        if (parts.isNotEmpty() && (!parts.last().contains('.') || parts.last().endsWith("(") || parts.last().isEmpty())) {
            if (inputString.isNotEmpty() && "+-*/".contains(inputString.last())) {
                return ""
            }
            if (inputString.isNotEmpty() && !"+-*/".contains(inputString.last())) {
                return "."
            }
        }
        return ""
    }

    private fun canAddOperatorOrPointAfterOpeningBracket(operator: Char): Boolean {
        return inputString.isNotEmpty() && inputString.last() == '(' && (operator == '.' || "+*/".contains(operator))
    }

    private fun handleOperatorButton(button: Button): String {
        if (canAddOperatorOrPointAfterOpeningBracket(button.text[0])) {
            return ""
        }

        if (inputString.isNotEmpty() && "+-*/".contains(inputString.last())) {
            return ""
        }

        return button.text.toString()
    }

    private fun handleLeftBracketButton(): String {
        if (inputString.isNotEmpty() && inputString.last().isDigit()) {
            return "*("
        }
        return "("
    }

    private fun handleRightBracketButton(): String {
        if (inputString.isNotEmpty() && inputString.last().isDigit()) {
            return ")"
        }
        return ""
    }

    fun openHistoryActivity(view: View) {
        val historyIntent = Intent(this, HistoryActivity::class.java)
        historyIntent.putStringArrayListExtra("historyList", ArrayList(historyList))
        startActivity(historyIntent)
    }

    private fun onFunctionButtonClick(button: Button) {
        val currentFunction = when (button.id) {
            R.id.button_log -> "log10"
            R.id.button_sin -> "sin"
            R.id.button_cos -> "cos"
            R.id.button_tan -> "tan"
            else -> ""
        }

        val lastFunctionIndex = inputString.lastIndexOf(currentFunction)
        if (lastFunctionIndex != -1) {
            val previousCharIndex = lastFunctionIndex + currentFunction.length
            val previousChar = if (previousCharIndex < inputString.length) inputString[previousCharIndex] else ' '

            if (currentFunction == "sin" || currentFunction == "cos" || currentFunction == "tan") {
                return
            }
        }

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
        val maxCharacters = 15
        val textSizeOriginal = 36f
        val textSizeSmall = 28f

        tvResult.text = ""

        if (inputString.isEmpty()) {
            tvResult.text = "0"
            return
        }

        if (inputString.length > maxCharacters) {
            val lines = inputString.count { it == '\n' } + 1
            if (lines < 2) {
                tvResult.append("\n$inputString")
                tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSmall)
            } else {
                val currentText = tvResult.text.toString()
                val newText = currentText.substring(currentText.indexOf('\n') + 1) + "\n$inputString"
                tvResult.text = newText
            }
        } else {
            tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeOriginal)
            tvResult.text = inputString
        }

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun appendResultText(text: String) {
        tvResult.append(text)
    }

    private fun calculateResult() {
        inputString += autoCloseBrackets()

        try {
            val result = ExpressionBuilder(inputString).build().evaluate()
            val epsilon = 1e-10

            val historyLine = when {
                Math.abs(result) > 1e16 -> "∞"
                Math.abs(result - result.toInt()) < epsilon -> String.format("%.0f", result)
                result.isNaN() -> "Некорректный ввод"
                else -> result.toString()
            }
            historyList.add("$inputString = $historyLine")

            // Обновляем строку результата
            inputString = historyLine
            appendResultText("\n") // Добавляем новую строку перед выводом следующего результата
        } catch (e: ArithmeticException) {
            inputString = "Ошибка"
        } catch (e: Exception) {
            inputString = "Ошибка"
        }
        updateResult()
    }
}