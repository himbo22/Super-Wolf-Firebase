package com.example.superwolffirebase.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Room(
    var name: String? = null,
    var amount: Int? = null,
    var days: Int? = null,
    var nights: Int? = null,
    var gameStarted: Boolean? = null
) : Parcelable
