package com.example.firstlab.Domains.FirebaseFiles

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreUtils(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val historyCollection = db.collection("history")
    private val lastResultCollection = db.collection("lastResult")

    fun saveHistoryAndLastResultToFirestore(historyList: List<String>, lastResult: Int) {
        saveHistoryToFirestore(historyList)
        //saveLastResultToFirestore(lastResult)
    }

    private fun saveHistoryToFirestore(historyList: List<String>) {
        val historyData = hashMapOf("historyList" to historyList)
        historyCollection.document("historyData").set(historyData)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                Toast.makeText(context, "Ошибка при сохранении истории в Firestore", Toast.LENGTH_SHORT).show()
            }
    }

//    private fun saveLastResultToFirestore(lastResult: Int) {
//        val lastResultData = hashMapOf("result" to lastResult)
//        lastResultCollection.document("result").set(lastResultData)
//            .addOnSuccessListener {
//            }
//            .addOnFailureListener {
//                Toast.makeText(context, "Ошибка при сохранении последнего результата в Firestore", Toast.LENGTH_SHORT).show()
//            }
//    }

    fun loadHistoryFromFirestore(historyList: MutableList<String>) {
        historyCollection.document("historyData").get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val history = document.get("historyList") as? List<String>
                    if (history != null) {
                        historyList.clear()
                        historyList.addAll(history)
                    }

                    val iterator = historyList.iterator()
                    while (iterator.hasNext()) {
                        val item = iterator.next()
                        if (history != null) {
                            if (!history.contains(item)) {
                                iterator.remove()
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Ошибка при загрузке истории из Firestore", Toast.LENGTH_SHORT).show()
            }
    }

//    fun loadLastResultFromFirestore(tvResult: TextView) {
//        val resultRef = db.collection("lastResult").document("result")
//        resultRef.get()
//            .addOnSuccessListener { document ->
//                if (document != null && document.exists()) {
//                    val lastResult = document.getLong("result")
//                    if (lastResult != null) {
//                        tvResult.text = lastResult.toString()
//                    } else {
//                        Toast.makeText(context, "Ошибка: последний результат не является числом", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(context, "Ошибка при загрузке последнего результата из Firestore", Toast.LENGTH_SHORT).show()
//            }
//    }



    fun clearHistoryInFirestore(function: (Any?) -> Unit) {
        historyCollection.document("historyData").delete()
            .addOnSuccessListener {
                Toast.makeText(context, "История успешно очищена в Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Ошибка при очистке истории в Firestore", Toast.LENGTH_SHORT).show()
            }
    }
}