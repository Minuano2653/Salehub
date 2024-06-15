package com.example.salehub.model.create_post

import android.net.Uri
import com.example.salehub.model.BaseRepository
import com.example.salehub.screens.account.OperationState
import kotlinx.coroutines.tasks.await

class FirebaseCreatePostRepository : BaseRepository() {

    suspend fun savePost(post: Post) : Result<OperationState> {
        return try {
            val currentUserId = auth.currentUser?.uid
            if (currentUserId == null) {
                Result.failure(IllegalStateException("User is not signed in"))
            } else {

                val newPostRef = db.collection("posts").document()
                newPostRef.set(post.copy(author = currentUserId)).await()

                val imageUrls = post.imageUrls.mapNotNull { uri ->
                    val uploadResult = uploadImageAndGetUrl(uri, newPostRef.id)
                    if (uploadResult.isSuccess) {
                        uploadResult.getOrNull()
                    } else {
                        return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Exception not found"))
                    }
                }

                // Обновляем поле imageUrls в документе
                if (imageUrls.isNotEmpty()) {
                    newPostRef.update("imageUrls", imageUrls).await()
                }

                Result.success(OperationState.SUCCESS)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    private suspend fun uploadImageAndGetUrl(imageUri: Uri, postId: String): Result<String> {
        val storageRef = storage.child("posts/$postId/${getCurrentTime()}.jpg")
        return try {
            val uploadTaskSnapshot = storageRef.putFile(imageUri).await()
            val downloadUrl = uploadTaskSnapshot.storage.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}