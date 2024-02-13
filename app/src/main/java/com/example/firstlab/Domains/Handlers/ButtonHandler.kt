package com.example.firstlab.Domains.Handlers

import android.widget.Button
import com.example.firstlab.R
import com.example.firstlab.Activitis.MainActivity

class ButtonClickListener(private val activity: MainActivity, private val resultHandler: ResultHandler) {

    fun onButtonClick(button: Button) {
        activity.apply {
            if (isMaxDigitsReached() && !isAddingDigitsAllowed(button)) {
                return
            }

            val functionHandler = FunctionHandler()
            val buttonText = when (button.id) {
                R.id.button_root -> "sqrt("
                R.id.button_point -> functionHandler.handlePoint(inputString)
                R.id.button_plus, R.id.button_minus, R.id.button_myltiply, R.id.button_divide -> handleOperatorButton(button)
                R.id.button_left_bracket -> functionHandler.handleLeftBracketButton(inputString)
                R.id.button_right_bracket -> functionHandler.handleRightBracketButton(inputString)
                else -> button.text.toString()
            }

            if (buttonText == "^" && !canAddPowerSymbol()) {
                return
            }
            else if (buttonText == "^" && lastInput[0] == ')') {
                inputString += buttonText
                lastInput = buttonText
                resultHandler.updateResult()
                return
            }


            if (button.id == R.id.button_point) {
                val lastNumber = inputString.split("[+\\-*/()]".toRegex()).lastOrNull()
                if (lastNumber != null && lastNumber.contains('.')) {
                    return
                }
                inputString += ""
            }

            if (buttonText[0].isDigit() || buttonText == ".") {
                currentNumberLength++
                if (currentNumberLength > MAX_CHARACTERS) {
                    return
                }
            } else {
                currentNumberLength = 0
            }

            if (inputString.startsWith("Ошибка:") || inputString.startsWith("Некорректный ввод")) {
                inputString = ""
            }

            if (inputString == "0" && buttonText[0].isDigit() || inputString == "0." && buttonText[0] == '.') {
                inputString = buttonText
            } else if (inputString.endsWith("0") && buttonText[0].isDigit() && lastInput.isEmpty()) {
                inputString = buttonText
            } else if (inputString.endsWith("0") && buttonText[0].isDigit() && lastInput[0] in "+-*/") {
                inputString = buttonText
            } else {
                inputString += buttonText
                lastInput = buttonText
                resultHandler.updateResult()
            }
        }
    }


    private fun handleOperatorButton(button: Button): String {
        val activity = activity
        if (activity.inputString.isNotEmpty() && "+-*/".contains(activity.inputString.last())) {
            activity.inputString = activity.inputString.dropLast(1)
        }
        return button.text.toString()
    }

    private fun isAddingDigitsAllowed(button: Button): Boolean {
        val activity = activity
        val currentInput = when (button.id) {
            R.id.button_plus, R.id.button_minus, R.id.button_myltiply,
            R.id.button_divide, R.id.button_pow, R.id.button_root,
            R.id.button_log, R.id.button_left_bracket, R.id.button_right_bracket -> false
            else -> true
        }

        return currentInput || (activity.lastInput.isNotEmpty() && activity.lastInput[0].isDigit() && activity.currentNumberLength < activity.MAX_CHARACTERS)
    }

    private fun isMaxDigitsReached(): Boolean {
        val activity = activity
        if (activity.isMaxDigitsExceeded) {
            return false
        }

        val parts = activity.inputString.split("[+\\-*/()]".toRegex())
        val isReached = parts.any { it.length > activity.MAX_CHARACTERS }

        activity.isMaxDigitsExceeded = isReached

        return isReached
    }

    private fun canAddPowerSymbol(): Boolean {
        val activity = activity
        return activity.inputString.isNotEmpty() && activity.inputString.last().isDigit()
    }

    fun onFunctionButtonClick(button: Button) {
        val activity = activity
        val currentFunction = when (button.id) {
            R.id.button_log -> "log10"
            R.id.button_sin -> "sin"
            R.id.button_cos -> "cos"
            R.id.button_tan -> "tan"
            else -> ""
        }

        val operators = charArrayOf('+', '-', '*', '/')

        for (charIndex in activity.inputString.indices.reversed()) {
            val char = activity.inputString[charIndex]
            if (char.isDigit() || char == '.') {
                activity.lastNumberIndex = charIndex
                break
            } else if (char in operators) {
                activity.lastOperatorIndex = charIndex
                break
            }
        }

        if (activity.lastNumberIndex > activity.lastOperatorIndex) {
            activity.inputString = activity.inputString.substring(0, activity.lastNumberIndex + 1) + "*" + activity.inputString.substring(activity.lastNumberIndex + 1)
        }

        val functionText = when (button.id) {
            R.id.button_log -> "log10("
            R.id.button_sin, R.id.button_cos, R.id.button_tan -> {
                if (!activity.isDegreeMode) {
                    "${button.text}("
                } else {
                    "${button.text}(π/180.0*"
                }
            }
            else -> button.text.toString() + "("
        }

        activity.inputString += functionText
        resultHandler.updateResult()
    }
}
