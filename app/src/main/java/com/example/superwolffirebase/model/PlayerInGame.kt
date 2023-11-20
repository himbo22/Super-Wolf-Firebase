package com.example.superwolffirebase.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PlayerInGame(
    var id: String? = null,
    var avatar: String? =null,
    var name: String? = null,
    var role: String? = null,
    var exposed: Boolean ?= null,
    var vote: String?= null,
    var voted: Int? = null
)
