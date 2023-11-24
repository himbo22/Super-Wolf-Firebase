package com.example.superwolffirebase.other

import androidx.recyclerview.widget.DiffUtil
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room

class OneRoomDiffUtil(
    private val oldList: Room,
    private val newList: Room
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return 1
    }

    override fun getNewListSize(): Int {
        return 1
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList.name == newList.name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList == newList
    }

}