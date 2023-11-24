package com.example.superwolffirebase.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlayerInGame(
    var id: String? = null,
    var avatar: String? = null,
    var name: String? = null,
    var role: String? = null,
    var dead: Boolean? = null,
    var vote: String? = null,
    var voted: Int? = null,
    var isShowRole: Boolean? = null,
    var expose: Boolean? = null,
    var ready: Boolean? = null
) : Parcelable
