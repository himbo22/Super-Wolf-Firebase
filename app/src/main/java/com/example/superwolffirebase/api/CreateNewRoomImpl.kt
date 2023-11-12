package com.example.superwolffirebase.api

import com.example.superwolffirebase.models.Room
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CreateNewRoomImpl  @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : CreateNewRoom {


    override suspend fun createNewRoom(name: String): Resource<DatabaseReference> {
        return suspendCoroutine { continuation ->
            val reference = firebaseDatabase.reference.child("rooms")
            val room = Room(name, 1)
            reference.child(name).setValue(room)
                .addOnCompleteListener {
                    continuation.resume(Resource.Success(firebaseDatabase.reference))
                }
                .addOnFailureListener { exception->
                    continuation.resume(Resource.Error(exception))
                }
        }
    }
}