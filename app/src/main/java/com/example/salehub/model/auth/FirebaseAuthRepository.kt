package com.example.salehub.model.auth

import com.example.salehub.model.BaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FirebaseAuthRepository : BaseRepository() {

    suspend fun signUpWithEmailAndPassword(email: String, password: String, nickname: String) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userID = authResult.user?.uid ?: throw IllegalStateException("User ID is null")
            createUserDocument(userID, email, nickname)
        } catch (e: Exception) {
            throw e
        }
    }

    fun isSingedIn() = auth.currentUser != null

    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    private suspend fun createUserDocument(userID: String, email: String, nickname: String) {
        val userData = mapOf(
            "email" to email,
            "nickname" to nickname,
            "registrationDate" to getCurrentFormattedDate(),
            "avatarUrl" to "",
            "favourites" to listOf<DocumentReference>(),
            "incremented" to listOf<String>(),
            "decremented" to listOf<String>(),
        )
        db.collection("users").document(userID).set(userData).await()
    }

    private fun getCurrentFormattedDate(): String {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        return formatter.format(calendar.time)
    }
}