package com.example.superwolffirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Room(
    var name: String? = null,
    var amount: Int? = null,
    var days: Int? = null,
    var nights: Int? = null,
    var gameStarted: Boolean? = null,
    var day: Boolean? = null,
    var gameEnded: Boolean? = null,
    var remaining: Int? = null,
    var playerCreateRoomId: String? = null,
    var harmPower: Boolean? = null,
    var healPower: Boolean? = null,
    var witchPhase: Boolean? = null
) : Parcelable
