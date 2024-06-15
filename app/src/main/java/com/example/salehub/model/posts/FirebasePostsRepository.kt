package com.example.salehub.model.posts

import android.util.Log
import com.example.salehub.model.BaseRepository
import com.example.salehub.model.create_post.Post
import com.example.salehub.screens.account.OperationState
import com.example.salehub.screens.posts.PostsFragment
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class FirebasePostsRepository : BaseRepository() {

    suspend fun fetchPostsByType(type: Int) : List<PostItem>? {
        return when(type) {
            PostsFragment.TYPE_MY_POSTS -> {
                fetchUserPosts()
            }
            PostsFragment.TYPE_FAVOURITE_POSTS -> {
                fetchFavouritePosts()
            }
            PostsFragment.TYPE_NEW_POSTS -> {
                fetchAllPosts()
            }
            PostsFragment.TYPE_BEST_POSTS -> {
                fetchBestPosts()
            }

            else -> {null}
        }
    }

    private suspend fun fetchUserPosts(): List<PostItem>? {
        return try {
            val userUid = auth.currentUser!!.uid
            val accountDocument = db.collection("users").document(userUid).get().await()

            val nickname = accountDocument.getString("nickname") ?: ""
            val avatarUrl = accountDocument.getString("avatarUrl") ?: ""

            val postsSnapshot = db
                .collection("posts")
                .whereEqualTo("author", userUid)
                .get()
                .await()

            val myPosts = postsSnapshot.documents.mapNotNull { document ->
                val post = document.toObject(PostItem::class.java)
                post?.copy(id = document.id, author = nickname, authorAvatar = avatarUrl)
            }

            Log.d("AAAA", myPosts.toString())

            myPosts

        } catch (e: Exception) {
            Log.d("RRRR", e.message.toString())
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchFavouritePosts() : List<PostItem>? {
        return try {
            val userUid = auth.currentUser!!.uid
            val userDocument = db.collection("users").document(userUid).get().await()

            val nickname = userDocument.getString("nickname") ?: ""
            val avatarUrl = userDocument.getString("avatarUrl") ?: ""

            val favouritesRefs = userDocument.get("favourites") as? List<DocumentReference> ?: emptyList()

            val favouritePosts = favouritesRefs.mapNotNull { docRef ->
                val postDocument = docRef.get().await()
                val post = postDocument.toObject(PostItem::class.java)
                post?.copy(id = postDocument.id, author = nickname, authorAvatar = avatarUrl)
            }

            favouritePosts
        } catch (e: Exception) {
            Log.d("RRRR", e.message.toString())
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchAllPosts() : List<PostItem>? {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        return try {
            val snapshots = db.collection("posts")
                .get()
                .await()

            val posts = snapshots.documents.mapNotNull { document ->
                val post = document.toObject(PostItem::class.java)
                post?.copy(id = document.id)
            }

            // Сортировка постов по дате
            val sortedPosts = posts.sortedByDescending { post ->
                dateFormat.parse(post.date)
            }

            sortedPosts
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchBestPosts() : List<PostItem>? {
        return try {
            val snapshots = db.collection("posts")
                .get()
                .await()

            val posts = snapshots.documents.mapNotNull { document ->
                val post = document.toObject(PostItem::class.java)
                post?.copy(id = document.id)
            }

            // Сортировка постов по рейтингу в порядке убывания
            val sortedPosts = posts.sortedByDescending { post ->
                post.rating
            }

            sortedPosts
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            e.printStackTrace()
            null
        }
    }

    suspend fun addToFavourite(postId: String) : Result<OperationState> {
        return try {
            val userUid = auth.currentUser!!.uid
            val userDocRef = db.collection("users").document(userUid)
            val userDocument = userDocRef.get().await()

            val favourites = userDocument.get("favourites") as? List<DocumentReference> ?: emptyList()

            val postRef = db.collection("posts").document(postId)

            if (favourites.contains(postRef)) {
                Result.success(OperationState.EMPTY)
            } else {
                val updatedFavourites = favourites + postRef
                userDocRef.update("favourites", updatedFavourites).await()
                Result.success(OperationState.SUCCESS)
            }
        } catch (e: Exception) {
            Log.d("AAAA", e.message.toString())
            Result.failure(e)
        }
    }

    suspend fun incrementPost(postId: String): Result<OperationState> {
        return try {
            val userUid = auth.currentUser!!.uid
            val userDocRef = db.collection("users").document(userUid)
            val userDocument = userDocRef.get().await()

            val incremented = userDocument.get("incremented") as? List<String> ?: emptyList()
            val decremented = userDocument.get("decremented") as? List<String> ?: emptyList()

            if (incremented.contains(postId)) {
                Result.success(OperationState.EMPTY) // Пост уже увеличен
            } else {
                val postDocRef = db.collection("posts").document(postId)
                db.runTransaction { transaction ->
                    val postSnapshot = transaction.get(postDocRef)
                    val currentRating = postSnapshot.getLong("rating") ?: 0
                    transaction.update(postDocRef, "rating", currentRating + 1)

                    val updatedIncremented = incremented + postId
                    val updatedDecremented = decremented - postId
                    transaction.update(userDocRef, "incremented", updatedIncremented)
                    transaction.update(userDocRef, "decremented", updatedDecremented)
                }.await()
                Result.success(OperationState.SUCCESS)
            }
        } catch (e: Exception) {
            Log.e("incrementPost", "Error incrementing post", e)
            Result.failure(e)
        }
    }

    suspend fun decrementPost(postId: String): Result<OperationState> {
        return try {
            val userUid = auth.currentUser!!.uid
            val userDocRef = db.collection("users").document(userUid)
            val userDocument = userDocRef.get().await()

            val incremented = userDocument.get("incremented") as? List<String> ?: emptyList()
            val decremented = userDocument.get("decremented") as? List<String> ?: emptyList()

            if (decremented.contains(postId)) {
                Result.success(OperationState.EMPTY) // Пост уже уменьшен
            } else {
                val postDocRef = db.collection("posts").document(postId)
                db.runTransaction { transaction ->
                    val postSnapshot = transaction.get(postDocRef)
                    val currentRating = postSnapshot.getLong("rating") ?: 0
                    transaction.update(postDocRef, "rating", currentRating - 1)

                    val updatedDecremented = decremented + postId
                    val updatedIncremented = incremented - postId
                    transaction.update(userDocRef, "incremented", updatedIncremented)
                    transaction.update(userDocRef, "decremented", updatedDecremented)
                }.await()
                Result.success(OperationState.SUCCESS)
            }
        } catch (e: Exception) {
            Log.e("decrementPost", "Error decrementing post", e)
            Result.failure(e)
        }
    }

}