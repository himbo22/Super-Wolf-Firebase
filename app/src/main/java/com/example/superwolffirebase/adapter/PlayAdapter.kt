package com.example.superwolffirebase.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ItemPlayerBinding
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.other.Constant
import com.example.superwolffirebase.other.Constant.WEREWOLF
import com.example.superwolffirebase.other.PlayDiffUtil
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show
import com.example.superwolffirebase.utils.showLog


@GlideModule
class PlayAdapter(private val id:String,private val listener: OnItemClick, private val roomName: String) :
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
                Glide.with(playAvatar.context).load(currentPlayer.avatar).into(playAvatar)

                Glide.with(avatarVote.context).load(currentPlayer.vote).into(avatarVote)

                playerView.setOnClickListener {
                    listener.vote(id, currentPlayer, roomName)
                }



//
//                if (player.vote.isNullOrBlank().not()) {
//                    constraintVote.show()
//                } else {
//                    constraintVote.invisible()
//                }

            }
        }





//        if (oldPlayer.id == currentPlayer.id) {
//
//            holder.binding.playerName.setTextColor(Color.parseColor("#f73e7d"))
//
//            if (currentPlayer.role == WEREWOLF) {
//                holder.binding.role.show()
//                holder.binding.role.setImageResource(R.drawable.werewolf_icon)
//            } else {
//                holder.binding.role.invisible()
//            }
//
//        } else {
//
//
//
//            if (oldPlayer.role == currentPlayer.role && oldPlayer.role == WEREWOLF) {
//                holder.binding.role.show()
//                holder.binding.role.setImageResource(R.drawable.werewolf_icon)
//            } else {
//                holder.binding.role.invisible()
//            }
//
//
//        }




    }

    fun setData(newPlayerList: List<PlayerInGame>) {
        val diffUtil = PlayDiffUtil(oldPlayerList, newPlayerList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldPlayerList = newPlayerList
        diffResult.dispatchUpdatesTo(this)
    }

//    fun setPlayerInGame(newPlayer: PlayerInGame){
//        val diffUtil = PlayerInGameDiffUtil(oldPlayer, newPlayer)
//        val diffResult = DiffUtil.calculateDiff(diffUtil)
//        oldPlayer = newPlayer
//        diffResult.dispatchUpdatesTo(this)
//    }


}

interface OnItemClick{
    fun vote(id: String,currentPlayer: PlayerInGame, roomName: String)
    fun unVote(player: PlayerInGame, roomName: String)
    fun changeVote(player: PlayerInGame, roomName: String)
}

