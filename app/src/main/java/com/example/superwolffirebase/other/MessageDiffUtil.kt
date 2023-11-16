package com.example.superwolffirebase.other

import androidx.recyclerview.widget.DiffUtil
import com.example.superwolffirebase.model.MessageRequest
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room

class MessageDiffUtil(
    private val oldList: List<MessageRequest>,
    private val newList: List<MessageRequest>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].time == newList[newItemPosition].time
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{

            oldList[oldItemPosition].time != newList[newItemPosition].time -> {
                false
            }
            oldList[oldItemPosition].playerName != newList[newItemPosition].playerName -> {
                false
            }
            oldList[oldItemPosition].time != newList[newItemPosition].time -> {
                false
            }


            else -> true
        }
    }
}