package com.example.superwolffirebase.api

import android.net.Uri
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.FirebaseDatabase

interface SetUpProfile {
    suspend fun uploadAvatar(avatar: Uri): Resource<FirebaseDatabase>


}