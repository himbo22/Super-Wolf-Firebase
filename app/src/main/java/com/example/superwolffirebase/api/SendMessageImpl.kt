package com.example.superwolffirebase.api

import com.example.superwolffirebase.model.MessageRequest
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SendMessageImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : SendMessage {
    override suspend fun sendMessage(
        roomName: String,
        message: String,
        playerName: String
    ): Resource<String> {
        return suspendCoroutine { continuation ->
            val time = System.currentTimeMillis()
            val ref = firebaseDatabase.reference.child("messages")
            val messageRequest = MessageRequest(message, playerName, time)
            ref.child(roomName).child(time.toString()).setValue(messageRequest).addOnSuccessListener {
                continuation.resume(Resource.Success(message))
            }.addOnFailureListener {
                continuation.resume(Resource.Error(it))
            }
        }
    }


}