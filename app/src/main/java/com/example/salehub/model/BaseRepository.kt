package com.example.salehub.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


open class BaseRepository {
    protected val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    protected val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    protected val storage: StorageReference by lazy { FirebaseStorage.getInstance().reference }
    protected val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance("https://salehub-25355-default-rtdb.europe-west1.firebasedatabase.app") }

    fun getCurrentTime() : String {
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val now = Date()
        return formatter.format(now)
    }
}