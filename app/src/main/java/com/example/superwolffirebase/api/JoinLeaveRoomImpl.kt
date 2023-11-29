package com.example.superwolffirebase.api

import android.provider.ContactsContract.Data
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
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
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class JoinLeaveRoomImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : JoinLeaveRoom {

    override suspend fun joinRoom(
        roomName: String,
        amount: Int,
        id: String,
        avatar: String,
        playerName: String,
        role: String
    ): Event<Resource<PlayerInGame>> {
        return suspendCoroutine { continuation ->
            val reference = firebaseDatabase.getReference("rooms/${roomName}")

//            reference.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val currentPlayers = snapshot.child("players").childrenCount.toInt()
//                    reference.child("amount").setValue(currentPlayers).addOnSuccessListener {
//                        val player = PlayerInGame(
//                            id,
//                            avatar,
//                            playerName,
//                            "",
//                            false,
//                            "",
//                            "",
//                            0,
//                            expose = false,
//                            ready = false
//                        )
//                        reference.child("players/${id}").setValue(player).addOnSuccessListener {
//                            continuation.resume(Event(Resource.Success(player)))
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    continuation.resume(Event(Resource.Error(error.toException())))
//
//                }
//
//            })


            reference.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentAmount = currentData.child("amount").getValue(Int::class.java) ?: 0

                    currentData.child("amount").value = currentAmount + 1
                    val player = PlayerInGame(
                        id,
                        avatar,
                        playerName,
                        "",
                        false,
                        "",
                        "",
                        0,
                        expose = false,
                        ready = false
                    )
                    reference.child("players/${id}").setValue(player)

//                    firebaseDatabase.getReference("rooms/${roomName}/players")
//                        .addValueEventListener(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                showLog(snapshot.childrenCount.toString())
//                                currentData.child("amount").value = snapshot.childrenCount
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                TODO("Not yet implemented")
//                            }
//
//                        })
                    continuation.resume(Event(Resource.Success(player)))
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (!committed || error != null) {
                        continuation.resume(Event(Resource.Error(Exception("Failed to join room"))))
                    }
                }

            })


        }
    }

    override suspend fun leaveRoom(
        roomName: String,
        amount: Int,
        id: String,
    ): Event<Resource<DatabaseReference>> {
        return suspendCoroutine { continuation ->
            val reference = firebaseDatabase.getReference("rooms").child(roomName)
            reference.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentAmount = currentData.child("amount").getValue(Int::class.java) ?: 0
                    currentData.child("amount").value = currentAmount - 1

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (currentData?.child("amount")?.getValue(Int::class.java) == 0) {
                        reference.removeValue().addOnSuccessListener {
                            continuation.resume(Event(Resource.Success(firebaseDatabase.reference)))
                        }.addOnFailureListener {
                            continuation.resume(Event(Resource.Error(it)))
                        }
                    } else {
                        reference.child("players/${id}").removeValue().addOnSuccessListener {
                            continuation.resume(Event(Resource.Success(firebaseDatabase.reference)))
                        }.addOnFailureListener {
                            continuation.resume(Event(Resource.Error(it)))
                        }
                    }
                }

            })

        }

    }


}