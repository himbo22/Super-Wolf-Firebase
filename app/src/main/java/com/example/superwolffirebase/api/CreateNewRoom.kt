package com.example.superwolffirebase.api

import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DatabaseReference

interface CreateNewRoom {

    suspend fun createNewRoom(
        name: String,
        playerId: String,
        avatar: String,
        playerName: String
    ): Event<Resource<Pair<Room, PlayerInGame>>>

}