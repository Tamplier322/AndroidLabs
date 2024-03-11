package com.example.firstlab.Domains.FirebaseFiles

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreManager(private val context: Context) {

    private val db = Firebase.firestore

    fun saveCurrentThemeToFirestore(theme: Int) {
        val themesRef = db.collection("theme")
        val data = hashMapOf("current_theme" to theme)
        themesRef.document("7rGDPSlMyggDXGsYwpnu").set(data)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                Toast.makeText(context, "Ошибка при сохранении темы в Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    fun getCurrentThemeFromFirestore(changeTheme: (Int) -> Unit) {
        val themesRef = db.collection("theme")
        themesRef.document("7rGDPSlMyggDXGsYwpnu").get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val theme = document.getLong("current_theme")
                    if (theme != null) {
                        changeTheme(theme.toInt())
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Ошибка при загрузке темы из Firestore", Toast.LENGTH_SHORT).show()
            }
    }
}
