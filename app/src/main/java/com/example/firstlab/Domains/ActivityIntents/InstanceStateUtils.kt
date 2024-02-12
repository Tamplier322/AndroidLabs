package com.example.firstlab.Domains.ActivityIntents

import android.os.Bundle
import com.example.firstlab.Activitis.MainActivity

class InstanceStateUtils(private val activity: MainActivity) {

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(activity.INPUT_STRING_KEY, activity.inputString)
        outState.putBoolean(activity.ANGLE_MODE_KEY, activity.isDegreeMode)
        outState.putStringArrayList(activity.HISTORY_LIST_KEY, ArrayList(activity.historyList.distinct()))
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        activity.inputString = savedInstanceState.getString(activity.INPUT_STRING_KEY, "")
        activity.historyList.clear()
        activity.historyList.addAll(savedInstanceState.getStringArrayList(activity.HISTORY_LIST_KEY) ?: emptyList())
    }
}
