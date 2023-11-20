package com.example.superwolffirebase.api

import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DatabaseReference

interface JoinLeaveRoom {
    suspend fun joinRoom(
        roomName: String,
        amount: Int,
        id: String,
        avatar: String,
        playerName: String,
        role: String
    ): Event<Resource<PlayerInGame>>

    suspend fun leaveRoom(
        roomName: String,
        amount: Int,
        id: String,
    ): Event<Resource<DatabaseReference>>
}