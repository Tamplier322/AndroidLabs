package com.example.firstlab

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyTextView: TextView
    private lateinit var btnClose: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        btnClose = findViewById(R.id.btnClose)

        historyTextView = findViewById(R.id.historyTextView)

        val historyList = intent.getStringArrayListExtra("historyList")

        if (historyList != null && historyList.isNotEmpty()) {
            val indexedHistory = historyList.mapIndexed { index, line -> "${index + 1}) $line" }

            historyTextView.text = indexedHistory.joinToString("\n")
        } else {
            historyTextView.text = "No history available"
        }

        btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

