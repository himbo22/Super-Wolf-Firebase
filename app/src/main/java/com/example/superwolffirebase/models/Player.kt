package com.example.superwolffirebase.models

import android.net.Uri

data class Player(
    val id: String,
    val name: String,
    val avatar: String,
    val gender: String,
    val email: String,
    val password: String? = null,
    val role: Roles,
    val status: Status
)

enum class Roles{
    None, Werewolf, Villager, Seer, Guard, Witch
}

enum class Status{
    None, Dead, Alive
}

