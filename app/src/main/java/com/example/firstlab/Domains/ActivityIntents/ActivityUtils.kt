package com.example.firstlab.Domains.ActivityIntents

import android.content.Context
import android.content.Intent
import com.example.firstlab.Activitis.HistoryActivity
import com.example.firstlab.Activitis.LevelActivity

class ActivityUtils(private val context: Context) {

    fun openLevelActivity() {
        val intent = Intent(context, LevelActivity::class.java)
        context.startActivity(intent)
    }

    fun openHistoryActivity(historyList: ArrayList<String>) {
        val intent = Intent(context, HistoryActivity::class.java)
        intent.putStringArrayListExtra("historyList", historyList)
        context.startActivity(intent)
    }
}
