package com.example.firstlab.Domains.Utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.example.firstlab.R

class LevelPopup(private val context: Context) {

    private lateinit var popupWindow: PopupWindow
    private var isPopupWindowOpen = false

    fun showInfoPopup() {
        if (isPopupWindowOpen) {
            return
        }

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.info_popup, null)

        val btnClose: ImageButton = popupView.findViewById(R.id.btnClose)
        btnClose.setOnClickListener {
            popupWindow.dismiss()
        }

        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = (context.resources.displayMetrics.heightPixels * 1 / 2)
        val focusable = true
        popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.animationStyle = R.style.PopupAnimation

        popupWindow.showAtLocation(popupView, Gravity.TOP, 0, 0)

        isPopupWindowOpen = true

        popupWindow.setOnDismissListener {
            isPopupWindowOpen = false
        }
    }
}
