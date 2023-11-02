package com.example.superwolffirebase.api

import com.example.superwolffirebase.other.Resource
import com.google.firebase.auth.FirebaseUser

interface BaseAuth {

    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signUp(name: String, email: String, password: String): Resource<FirebaseUser>

    fun logOut()

}