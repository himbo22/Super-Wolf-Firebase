package com.example.superwolffirebase.other

import androidx.recyclerview.widget.DiffUtil
import com.example.superwolffirebase.models.Room

class MyDiffUtil(
    private val oldList: List<Room>,
    private val newList: List<Room>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{

            oldList[oldItemPosition].name != newList[newItemPosition].name -> {
                false
            }
            oldList[oldItemPosition].amount != newList[newItemPosition].amount -> {
                false
            }
            else -> true
        }
    }
}