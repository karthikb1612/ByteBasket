package com.example.bytebasket.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.Calendar

@Composable
fun Header(modifier: Modifier = Modifier) {
    var name by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("user").
        document(FirebaseAuth.getInstance().currentUser?.uid!!).
        get().addOnCompleteListener {
            name=it.result.get("name").toString()
        }
    }
    Column {
        val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning, $name! ☀️"
            in 12..16 -> "Good afternoon, $name! 🛒"
            in 17..21 -> "Good evening, $name! ✨"
            else -> "Late-night browsing, $name? 🌙"
        }

        Text(
            text = greeting,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            color = Color.DarkGray
        )
    }
}