package com.example.superwolffirebase.api

import com.example.superwolffirebase.other.Resource

interface SendMessage {
    suspend fun sendMessage(roomName: String, message: String, playerName: String) : Resource<String>
}