package com.example.superwolffirebase.api

import android.util.Log
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import javax.inject.Inject

class BaseAuthImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : BaseAuth {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Event<Resource<FirebaseUser>> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Event(Resource.Success(result.user!!))
        } catch (e: Exception) {
            Event(Resource.Error(e))
        }

    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Event<Resource<FirebaseUser>> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )?.await()
            Event(Resource.Success(result.user!!))

        } catch (e: Exception) {
            Log.d("hoangdeptrai", e.message.toString())
            e.printStackTrace()
            Event(Resource.Error(e))
        }
    }

    override fun logOut() {
        firebaseAuth.signOut()
    }
}