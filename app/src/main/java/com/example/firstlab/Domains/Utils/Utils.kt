// Utils.kt
package com.example.firstlab.Domains.Utils

import android.widget.Button
import com.example.firstlab.Activitis.MainActivity

class Utils(private val activity: MainActivity) {

    fun updateAngleModeButton(isDegreeMode: Boolean) {
        val buttonAngleMode = activity.findViewById<Button>(com.example.firstlab.R.id.button_angle_mode)
        buttonAngleMode.text = if (isDegreeMode) "Deg" else "Rad"
    }


    fun autoCloseBrackets(inputString: String): String {
        var openBracketsCount = 0
        var closeBracketsCount = 0

        for (char in inputString) {
            if (char == '(') {
                openBracketsCount++
            } else if (char == ')') {
                if (openBracketsCount > 0) {
                    openBracketsCount--
                } else {
                    continue
                }
            }
        }

        val additionalClosingBrackets = maxOf(0, openBracketsCount - closeBracketsCount)
        return inputString + ")".repeat(additionalClosingBrackets)
    }

}
