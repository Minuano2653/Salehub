package com.example.salehub.model.account

import android.net.Uri
import android.util.Log
import com.example.salehub.model.BaseRepository
import com.example.salehub.model.create_post.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class FirebaseAccountRepository : BaseRepository() {

    suspend fun uploadImage(imageUri: Uri) {
        val userUid = auth.currentUser!!.uid
        try {
            val userImageRef = storage.child("users/user-$userUid/avatar.jpg")
            val uploadTask = userImageRef.putFile(imageUri).await()
            val imageUrl = uploadTask.storage.downloadUrl.await().toString()
            db.collection("users").document(userUid).update("avatarUrl", imageUrl).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun fetchAccountInfo() : Account? {
        return try {
            val userUid = auth.currentUser!!.uid
            val accountDocument = db.collection("users").document(userUid).get().await()
            val account = accountDocument.toObject(Account::class.java)
            account

        } catch (e: Exception) {
            throw e
        }
    }

    fun signOut() {
        auth.signOut()
    }
}