package com.example.superwolffirebase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superwolffirebase.databinding.ItemPlayerBinding
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.other.MyDiffUtil
import com.example.superwolffirebase.other.PlayDiffUtil

class PlayAdapter(private val context: Context) : RecyclerView.Adapter<PlayAdapter.PlayViewHolder>() {

    private var oldPlayerList = emptyList<PlayerInGame>()

    inner class PlayViewHolder(val binding: ItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayViewHolder {
        return PlayViewHolder(ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return oldPlayerList.size
    }

    override fun onBindViewHolder(holder: PlayViewHolder, position: Int) {
        val currentPlayer = oldPlayerList[position]
        holder.apply {
            binding.apply {
                cardinalNumber.text = currentPlayer.cardinalNumber.toString()
                playerName.text = currentPlayer.name
                Glide.with(context).load(currentPlayer.avatar).into(playAvatar)
            }
        }
    }

    fun setData(newPlayerList: List<PlayerInGame>){
        val diffUtil = PlayDiffUtil(oldPlayerList, newPlayerList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldPlayerList = newPlayerList
        diffResult.dispatchUpdatesTo(this)
    }

}