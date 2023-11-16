package com.example.superwolffirebase.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.superwolffirebase.databinding.ItemMessageBinding
import com.example.superwolffirebase.model.MessageRequest
import com.example.superwolffirebase.other.MessageDiffUtil
import com.example.superwolffirebase.other.PlayDiffUtil

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var oldListMessage = emptyList<MessageRequest>()
    inner class MessageViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return oldListMessage.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = oldListMessage[position]
        holder.apply {
            binding.apply {
                itemName.text = "${currentMessage.playerName.toString()}:"
                itemMessage.text = currentMessage.message.toString()
            }
        }
    }

    fun setData(newMessageList: List<MessageRequest>){
        val diffUtil = MessageDiffUtil(oldListMessage, newMessageList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldListMessage = newMessageList
        diffResult.dispatchUpdatesTo(this)
    }
}