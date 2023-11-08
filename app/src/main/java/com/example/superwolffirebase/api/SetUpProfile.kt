package com.example.superwolffirebase.api

import android.net.Uri
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DatabaseReference

interface SetUpProfile {
    suspend fun uploadProfile(
        avatar: Uri,
        id: String,
        name: String,
        gender: String,
        email: String,
        role: String,
        status: String
    ) : Resource<DatabaseReference>

}