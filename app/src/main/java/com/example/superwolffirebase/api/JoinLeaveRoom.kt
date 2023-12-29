package com.example.superwolffirebase.api

import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DatabaseReference

interface JoinLeaveRoom {
    suspend fun joinRoom(
        roomName: String,
        id: String,
        avatar: String,
        playerName: String,
    ): Event<Resource<PlayerInGame>>





    suspend fun leaveRoom(
        roomName: String,
        id: String,
    ): Event<Resource<DatabaseReference>>



}