package com.example.superwolffirebase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ItemPlayerBinding
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.other.MyDiffUtil
import com.example.superwolffirebase.other.PlayDiffUtil
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@GlideModule
class PlayAdapter(private val context: Context) :
    RecyclerView.Adapter<PlayAdapter.PlayViewHolder>() {

    private var oldPlayerList = emptyList<PlayerInGame>()

    inner class PlayViewHolder(val binding: ItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayViewHolder {
        return PlayViewHolder(
            ItemPlayerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return oldPlayerList.size
    }

    override fun onBindViewHolder(holder: PlayViewHolder, position: Int) {
        val currentPlayer = oldPlayerList[position]
        holder.apply {
            binding.apply {
                playerName.text = currentPlayer.name
                Glide.with(context).load(currentPlayer.avatar).into(playAvatar)
                if (currentPlayer.exposed == false){
                    role.invisible()
                } else {
                    role.show()
                    if (currentPlayer.role == "Werewolf"){
                        role.setImageResource(R.drawable.werewolf_icon)
                    }
                }
            }
        }
    }

    fun setData(newPlayerList: List<PlayerInGame>) {
        val diffUtil = PlayDiffUtil(oldPlayerList, newPlayerList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldPlayerList = newPlayerList
        diffResult.dispatchUpdatesTo(this)
    }

}