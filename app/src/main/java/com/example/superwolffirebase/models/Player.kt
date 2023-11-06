package com.example.superwolffirebase.models

data class Player(
    var id: String,
    var name: String,
    var avatar: String,
    var gender: String,
    var email: String,
    var role: String? = null,
    var status: String? = null
)


