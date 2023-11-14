package com.example.superwolffirebase.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Room(
    var name: String? = null,
    var amount: Int? = null,
) : Parcelable
