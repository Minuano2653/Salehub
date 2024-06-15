package com.example.salehub.model.posts

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostItem(
    val id: String = "",
    var author: String = "",
    val authorAvatar: String = "",
    val date: String = "",
    val name: String = "",
    val oldCost: String = "",
    val newCost: String = "",
    val description: String = "",
    val link: String = "",
    val address: String = "",
    val category: String = "",
    val imageUrls: List<String> = emptyList(),
    var rating: Int = 0
) : Parcelable
