package com.example.salehub.model.create_post

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    var author: String = "",
    val name: String = "",
    val date: String = "",
    val oldCost: String = "",
    val newCost: String = "",
    val description: String = "",
    val link: String = "",
    val address: String = "",
    val category: String = "",
    val imageUrls: List<Uri> = emptyList(),
    val rating: Int = 0
) : Parcelable