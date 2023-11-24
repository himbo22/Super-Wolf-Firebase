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
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Constant
import com.example.superwolffirebase.other.Constant.WEREWOLF
import com.example.superwolffirebase.other.OneRoomDiffUtil
import com.example.superwolffirebase.other.PlayDiffUtil
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show
import com.example.superwolffirebase.utils.showLog
import okhttp3.internal.notify


@GlideModule
class PlayAdapter(
    private val id: String,
    private val listener: OnItemClick,

    ) :
    RecyclerView.Adapter<PlayAdapter.PlayViewHolder>() {

    private var oldPlayerList = emptyList<PlayerInGame>()
    private var room = Room()
    private var me = PlayerInGame()


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



                playerView.setOnClickListener {
                    listener.vote(currentPlayer)
                }


                if (room.gameStarted == true) {

                    ready.invisible()
                } else {
                    if (currentPlayer.ready == true) {
                        ready.show()
                    } else {
                        ready.invisible()
                    }
                }

//                if (currentPlayer.ready == true) {
//                    ready.show()
//                } else {
//                    ready.invisible()
//                }


                if (currentPlayer.voted != 0) {
                    constraintVoted.show()
                    tvVoted.text = currentPlayer.voted.toString()
                } else {
                    constraintVoted.invisible()
                }

                if (currentPlayer.vote.isNullOrBlank().not()) {
                    constraintVote.show()
                    Glide.with(avatarVote.context).load(currentPlayer.vote).into(avatarVote)
                } else {
                    constraintVote.invisible()
                }

                if (currentPlayer.expose == true) {
                    role.show()
                }

                if (currentPlayer.dead == true) {
                    playerView.isClickable = false
                    playerView.isFocusable = false
                    playAvatar.setImageResource(R.drawable.rip)
                } else {
                    Glide.with(playAvatar.context).load(currentPlayer.avatar).into(playAvatar)

                }

                if (currentPlayer.isShowRole == true) {
                    role.show()
                    role.setImageResource(R.drawable.werewolf_icon)
                } else if (me.role == "") {
                    role.invisible()
                }


            }
        }


    }


    fun setRoom(newRoom: Room) {
        val diffUtil = OneRoomDiffUtil(room, newRoom)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        room = newRoom
        diffResult.dispatchUpdatesTo(this)
    }

    fun setData(newPlayerList: List<PlayerInGame>) {

        me = newPlayerList.first {
            it.id == id
        }

        val diffUtil = PlayDiffUtil(oldPlayerList, newPlayerList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldPlayerList = newPlayerList
        diffResult.dispatchUpdatesTo(this)
    }


}

interface OnItemClick {
    fun vote(currentPlayer: PlayerInGame)
    fun unVote(player: PlayerInGame, roomName: String)
    fun changeVote(player: PlayerInGame, roomName: String)
}

