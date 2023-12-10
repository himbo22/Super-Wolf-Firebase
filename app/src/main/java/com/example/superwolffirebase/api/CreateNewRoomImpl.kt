package com.example.superwolffirebase.api

import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.showLog
import com.google.android.gms.measurement_base.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CreateNewRoomImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : CreateNewRoom {


    override suspend fun createNewRoom(
        name: String,
        playerId: String,
        avatar: String,
        playerName: String
    ): Event<Resource<Pair<Room, PlayerInGame>>> {
        return suspendCoroutine { continuation ->
            val room =
                Room(
                    name,
                    amount = 0,
                    days = 0,
                    nights = 0,
                    gameStarted = false,
                    gameEnded = false,
                    day  = true,
                    remaining = 0,
                    playerCreateRoomId = playerId,
                    harmPower  = true,
                    healPower = true,
                    witchPhase = false
                )

            val player = PlayerInGame(
                id = playerId,
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

            firebaseDatabase.getReference("rooms/${name}").setValue(room).addOnSuccessListener {
                firebaseDatabase.getReference("rooms/${name}").updateChildren(
                    mapOf(
                        "amount" to 1,
                        "players/${playerId}" to player
                    )
                )
            }


            continuation.resume(Event(Resource.Success(Pair(room, player))))
        }
    }
}