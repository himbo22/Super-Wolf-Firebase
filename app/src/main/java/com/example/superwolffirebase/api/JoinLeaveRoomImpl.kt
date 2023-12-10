package com.example.superwolffirebase.api

import android.provider.ContactsContract.Data
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.await
import com.example.superwolffirebase.utils.showLog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class JoinLeaveRoomImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : JoinLeaveRoom {

    override suspend fun joinRoom(
        roomName: String,
        id: String,
        avatar: String,
        playerName: String,
    ): Event<Resource<PlayerInGame>> = suspendCancellableCoroutine { continuation ->
        val reference = firebaseDatabase.getReference("rooms/${roomName}")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val currentAmount = snapshot.child("amount").getValue<Int>()
                    if (currentAmount != null) {

                        val player = PlayerInGame(
                            id = id,
                            avatar = avatar,
                            name = playerName,
                            role = "",
                            dead = false,
                            voteAvatar = "",
                            voteId = "",
                            voted = 0,
                            expose = false,
                            ready = false,
                            protected = false,
                            savedByWitch = false
                        )

                        reference.updateChildren(
                            mapOf(
                                "amount" to currentAmount + 1,
                                "players/${id}" to player
                            )
                        ).addOnSuccessListener {
                            continuation.resume(Event(Resource.Success(player)))
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(Event(Resource.Error(error.toException())))
            }

        })


    }



    override suspend fun leaveRoom(
        roomName: String,
        id: String,
    ): Event<Resource<DatabaseReference>> = suspendCancellableCoroutine { continuation ->
        val reference = firebaseDatabase.getReference("rooms/${roomName}")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gameStarted = snapshot.child("gameStarted").getValue<Boolean>()
                    if (gameStarted != null) {
                        if (gameStarted == true) {
                            reference.child("players/${id}/dead").setValue(true)
                        } else {
                            val currentAmount = snapshot.child("amount").getValue(Int::class.java)
                            if (currentAmount != null) {
                                reference.updateChildren(
                                    mapOf(
                                        "amount" to currentAmount - 1
                                    )
                                )
                            } else {
                                showLog("JoinLeaveRoomImpl 102")
                            }
                        }
                        continuation.resume(Event(Resource.Success(firebaseDatabase.reference)))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(Event(Resource.Error(error.toException())))
            }

        })

    }

    override suspend fun leaveRoomWhenClickBackButton(
        roomName: String,
        id: String,
    ): Event<Resource<DatabaseReference>> = suspendCancellableCoroutine { continuation ->
        val reference = firebaseDatabase.getReference("rooms").child(roomName)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gameStarted = snapshot.child("gameStarted").getValue<Boolean>()
                    if (gameStarted != null) {
                        if (gameStarted == true) {
                            reference.child("players/${id}/dead").setValue(true)
                        } else {
                            val currentAmount = snapshot.child("amount").getValue(Int::class.java)
                            if (currentAmount != null) {
                                reference.updateChildren(
                                    mapOf(
                                        "amount" to currentAmount - 1
                                    )
                                )
                            } else {
                                showLog("JoinLeaveRoomImpl 102")
                            }
                        }
                        continuation.resume(Event(Resource.Success(firebaseDatabase.reference)))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(Event(Resource.Error(error.toException())))
            }

        })
    }


}