package com.example.firstlab.Domains.Utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import com.example.firstlab.Domains.FirebaseFiles.getBackgroundColor
import com.example.firstlab.Domains.FirebaseFiles.getColorPrimary
import com.example.firstlab.R

fun showThemeSelectionDialog(context: Context, changeTheme: (Int) -> Unit) {
    val inflater = LayoutInflater.from(context)
    val dialogView = inflater.inflate(R.layout.theme_selection_dialog, null)

    val dialogBuilder = AlertDialog.Builder(context)
        .setView(dialogView)

    val dialog = dialogBuilder.create()

    val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
    dialog.window?.attributes?.windowAnimations = R.style.SlideInAnimation

    val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)
    btnClose.setOnClickListener {
        dialog.dismiss()
    }

    val themes = arrayOf(
        R.id.button_theme_1,
        R.id.button_theme_2,
        R.id.button_theme_3,
        R.id.button_theme_4
    )

    val colorCircles = arrayOf(
        R.id.color_circle_1,
        R.id.color_circle_2,
        R.id.color_circle_3,
        R.id.color_circle_4
    )

    for (i in themes.indices) {
        val themeButton = dialogView.findViewById<Button>(themes[i])
        themeButton.setOnClickListener {
            changeTheme(i + 1)
            dialog.dismiss()
        }

        val colorCircle = dialogView.findViewById<View>(colorCircles[i])
        val colorPrimary = getColorPrimary(context, i + 1)
        val backgroundColor = getBackgroundColor(context, i + 1)
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(colorPrimary, backgroundColor)
        )
        gradientDrawable.shape = GradientDrawable.OVAL
        colorCircle.background = gradientDrawable
    }

    dialog.show()
}
