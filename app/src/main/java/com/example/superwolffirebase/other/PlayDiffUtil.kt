package com.example.superwolffirebase.other

import androidx.recyclerview.widget.DiffUtil
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room

class PlayDiffUtil(
    private val oldList: List<PlayerInGame>,
    private val newList: List<PlayerInGame>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}