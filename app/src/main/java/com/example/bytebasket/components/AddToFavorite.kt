package com.example.bytebasket.components

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore



fun AddToFavorite(productId: String, context: Context) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid!!
    val userDoc = com.google.firebase.Firebase.firestore.collection("user").document(userId)

    userDoc.get().addOnSuccessListener { it ->
        val existing = it.get("favorite") as? List<String> ?: emptyList()
        val updatedList = existing.toMutableList().apply {
            if (!contains(productId)) add(productId)
        }

        userDoc.update("favorite", updatedList)
    }
}

fun RemoveFromFavorite(productId: String, context: Context) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid!!
    val userDoc = com.google.firebase.Firebase.firestore.collection("user").document(userId)

    userDoc.get().addOnSuccessListener { it ->
        val existing = it.get("favorite") as? List<String> ?: emptyList()
        val updatedList = existing.filter { it != productId }

        userDoc.update("favorite", updatedList)
    }
}
