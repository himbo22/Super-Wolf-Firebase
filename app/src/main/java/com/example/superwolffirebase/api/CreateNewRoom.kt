package com.example.superwolffirebase.api

import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DatabaseReference

interface CreateNewRoom {

    suspend fun createNewRoom(name: String, playerId: String) : Resource<DatabaseReference>

}