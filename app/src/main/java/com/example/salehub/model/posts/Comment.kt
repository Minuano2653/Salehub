package com.example.salehub.model.posts

data class Comment(
    var id: String = "",
    val postId: String = "",
    var userId: String = "",
    var userName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
