package com.example.salehub

import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Calendar

fun Fragment.getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd.MM.yy")
    return dateFormat.format(calendar.time)
}