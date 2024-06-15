package com.example.salehub

import com.example.salehub.model.account.FirebaseAccountRepository
import com.example.salehub.model.auth.FirebaseAuthRepository
import com.example.salehub.model.create_post.FirebaseCreatePostRepository
import com.example.salehub.model.edit_account.FirebaseEditAccountRepository
import com.example.salehub.model.posts.FirebasePostsRepository

object Repositories {
    val firebaseAuthRepository: FirebaseAuthRepository by lazy { FirebaseAuthRepository() }
    val accountRepository: FirebaseAccountRepository by lazy { FirebaseAccountRepository() }
    val createPostRepository: FirebaseCreatePostRepository by lazy { FirebaseCreatePostRepository() }
    val editAccountRepository: FirebaseEditAccountRepository by lazy { FirebaseEditAccountRepository() }
    val postsRepository: FirebasePostsRepository by lazy { FirebasePostsRepository() }
}