package com.example.firstlab

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyTextView = findViewById(R.id.historyTextView)

        // Получаем историю операций из Intent
        val history = getHistoryFromIntent()
        // Отображаем историю в TextView
        historyTextView.text = history
    }

    private fun getHistoryFromIntent(): String {
        // Получаем строку истории из Intent, если она передана
        val history = intent.getStringExtra("history")
        return history ?: "No history available"
    }

    // Дополнительный метод для обновления истории при каждом открытии HistoryActivity
    private fun updateHistory() {
        val updatedHistory = getHistoryFromIntent()
        historyTextView.text = updatedHistory
    }

    // Добавляем обновление истории при каждом запуске Activity
    override fun onResume() {
        super.onResume()
        updateHistory()
    }
}
