package com.example.salehub.model.account

import android.os.Parcelable
import com.example.salehub.model.create_post.Post
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    val nickname: String = "",
    val email: String = "",
    val registrationDate: String = "",
    val avatarUrl: String = "",
) : Parcelable