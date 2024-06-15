package com.example.salehub.model.edit_account

import android.net.Uri
import android.util.Log
import com.example.salehub.model.BaseRepository
import com.example.salehub.screens.account.OperationState
import kotlinx.coroutines.tasks.await

class FirebaseEditAccountRepository : BaseRepository() {

    suspend fun updateAccountInfo(nickname: String, imageUri: Uri?) : Result<OperationState> {
        return try {
            val userId = auth.currentUser!!.uid

            val userUpdates = hashMapOf<String, Any>(
                "nickname" to nickname
            )

            imageUri?.let {
                val userAvatarImageRef = storage.child("users/$userId/avatar.jpg")
                val uploadTask = userAvatarImageRef.putFile(it).await()
                val url = uploadTask.storage.downloadUrl.await().toString()

                userUpdates["avatarUrl"] = url
            }
            Log.d("RRRR", nickname)
            Log.d("RRRR", imageUri.toString())

            db.collection("users").document(userId).update(userUpdates).await()

            Result.success(OperationState.SUCCESS)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}