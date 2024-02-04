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

        val historyList = intent.getStringArrayListExtra("historyList")

        if (historyList != null && historyList.isNotEmpty()) {
            // Используем индексацию для каждой строки в истории
            val indexedHistory = historyList.mapIndexed { index, line -> "${index + 1}) $line" }

            // Объединяем строки с использованием перевода строки
            historyTextView.text = indexedHistory.joinToString("\n")
        } else {
            historyTextView.text = "No history available"
        }
    }
}

