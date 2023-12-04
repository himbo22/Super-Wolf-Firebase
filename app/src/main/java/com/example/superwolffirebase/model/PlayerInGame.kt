package com.example.superwolffirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerInGame(
    var id: String? = null,
    var avatar: String? = null,
    var name: String? = null,
    var role: String? = null,
    var dead: Boolean? = null,
    var voteAvatar: String? = null,
    var voteId: String? = null,
    var voted: Int? = null,
    var expose: Boolean? = null,
    var ready: Boolean? = null,
    var protected: Boolean? = null,
    var savedByWitch: Boolean? = null,
    var killByWitch: Boolean?=null
) : Parcelable
