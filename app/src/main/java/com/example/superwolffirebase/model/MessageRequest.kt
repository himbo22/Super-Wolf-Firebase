package com.example.superwolffirebase.model

data class MessageRequest(
    var message: String? = null,
    var playerName: String? = null,
    var time: Long? = null
)
