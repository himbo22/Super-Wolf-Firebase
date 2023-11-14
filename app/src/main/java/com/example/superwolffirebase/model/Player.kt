package com.example.superwolffirebase.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Player(
    var id: String? = null,
    var name: String? = null,
    var avatar: String? = null,
    var gender: String? = null,
    var email: String? = null
) : Parcelable


