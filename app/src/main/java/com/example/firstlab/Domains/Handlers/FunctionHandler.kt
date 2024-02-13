package com.example.firstlab.Domains.Handlers

import android.widget.Button

class FunctionHandler {
    fun handlePoint(inputString: String): String {
        if (inputString.isEmpty()) {
            return "0."
        }

        val lastChar = inputString.last()

        if (lastChar.isDigit() || lastChar == ')' || lastChar == 'e') {
            val lastNumber = inputString.split("[+\\-*/]".toRegex()).last()
            if (!lastNumber.contains('.')) {
                return "."
            }
        } else if (lastChar == '(' || lastChar in "+-*/") {
            return "0."
        }

        return "."
    }


    fun handleLeftBracketButton(inputString: String): String {
        if (inputString.isNotEmpty() && inputString.last().isDigit()) {
            return "*("
        }
        return "("
    }

    fun handleRightBracketButton(inputString: String): String {
        if (inputString.isNotEmpty() && inputString.last().isDigit()) {
            return ")"
        } else if (inputString.isNotEmpty() && inputString.last() == ')') {
            return "^"
        }
        return ")"
    }



}
