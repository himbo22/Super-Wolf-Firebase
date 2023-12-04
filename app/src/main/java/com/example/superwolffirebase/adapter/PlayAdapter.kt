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
import com.example.superwolffirebase.other.Constant.GUARD
import com.example.superwolffirebase.other.Constant.SEER
import com.example.superwolffirebase.other.Constant.VILLAGER
import com.example.superwolffirebase.other.Constant.WEREWOLF
import com.example.superwolffirebase.other.Constant.WITCH
import com.example.superwolffirebase.other.OnItemClick
import com.example.superwolffirebase.other.OneRoomDiffUtil
import com.example.superwolffirebase.other.PlayDiffUtil
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show
import com.example.superwolffirebase.utils.showLog
import okhttp3.internal.notify
import kotlin.math.log


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




                if (room.gameEnded == true) {
                    playerRole.show()
                }

                if (room.isDay == true) { // day
                    playerView.setOnClickListener {
                        when (me.voteId) {
                            "" -> {
                                listener.vote(currentPlayer)
                            }

                            currentPlayer.id -> {
                                listener.unVote(currentPlayer)
                            }

                            else -> {
                                listener.changeVote(currentPlayer)
                            }
                        }
                    }
                } else { // night
                    if (room.witchPhase == true) {
                        if (me.role == WITCH) {
                            listener.witchPhase()
                            if (room.harmPower == true && me != currentPlayer) {
                                playerView.setOnClickListener {
                                    when (me.voteId) {
                                        "" -> listener.witchPick(currentPlayer)
                                        currentPlayer.id -> listener.witchUnPick(currentPlayer)
                                        else -> listener.witchChangePick(currentPlayer)
                                    }
                                }
                            }
                        } else if (me.role == GUARD) {
                            playerView.setOnClickListener {  }

                        } else if (me.role == WEREWOLF) {
                            playerView.setOnClickListener {  }
                        }

                    } else {
                        if (me.role == SEER) { // can not expose itself role
                            if (currentPlayer != me) {
                                playerView.setOnClickListener {
                                    listener.seerPick(currentPlayer)
                                }
                            }
                        } else if (me.role == WEREWOLF) { // can not vote alies
                            if (currentPlayer.role != WEREWOLF && currentPlayer != me) {
                                playerView.setOnClickListener {
                                    when (me.voteId) {
                                        "" -> {
                                            listener.werewolfVote(currentPlayer)
                                        }

                                        currentPlayer.id -> {
                                            listener.werewolfUnVote(currentPlayer)
                                        }

                                        else -> {
                                            listener.werewolfChangeVote(currentPlayer)
                                        }
                                    }
                                }
                            }
                        } else if (me.role == GUARD) {
                            playerView.setOnClickListener {
                                when (me.voteId) {
                                    "" -> listener.guardVote(currentPlayer)
                                    currentPlayer.id -> listener.guardUnVote(currentPlayer)
                                    else -> listener.guardChangeVote(currentPlayer)
                                }
                            }
                        } else if (me.role == VILLAGER) {
                            playerView.setOnClickListener {

                            }
                        }
                    }


                }


                // show vote
                if (room.isDay == true) { // day
                    if (currentPlayer.voted != 0) {
                        constraintVoted.show()
                        tvVoted.text = currentPlayer.voted.toString()
                    } else {
                        constraintVoted.invisible()
                    }

                    if (currentPlayer.voteAvatar.isNullOrBlank().not()) {
                        constraintVote.show()
                        Glide.with(avatarVote.context).load(currentPlayer.voteAvatar)
                            .into(avatarVote)
                    } else {
                        constraintVote.invisible()
                    }
                } else { // night
                    if (me.role == WEREWOLF && currentPlayer.role == WEREWOLF) { // if werewolf
                        if (currentPlayer.voted != 0) {
                            constraintVoted.show()
                            tvVoted.text = currentPlayer.voted.toString()
                        } else {
                            constraintVoted.invisible()
                        }

                        if (currentPlayer.voteAvatar.isNullOrBlank().not()) {
                            constraintVote.show()
                            Glide.with(avatarVote.context).load(currentPlayer.voteAvatar)
                                .into(avatarVote)
                        } else {
                            constraintVote.invisible()
                        }
                    }

                    if (me == currentPlayer) {
                        if (currentPlayer.voted != 0) {
                            constraintVoted.show()
                            tvVoted.text = currentPlayer.voted.toString()
                        } else {
                            constraintVoted.invisible()
                        }

                        if (currentPlayer.voteAvatar.isNullOrBlank().not()) {
                            constraintVote.show()
                            Glide.with(avatarVote.context).load(currentPlayer.voteAvatar)
                                .into(avatarVote)
                        } else {
                            constraintVote.invisible()
                        }
                    }
                }




                if (currentPlayer.dead == true) {
                    playAvatar.setImageResource(R.drawable.rip)
                    playerView.isClickable = false
                    playerView.isFocusable = false
                } else {
                    playerView.isClickable = true
                    Glide.with(playAvatar.context).load(currentPlayer.avatar).into(playAvatar)
                }


                if (room.gameStarted == true) {
                    if (currentPlayer.dead == true) {
                        playAvatar.setImageResource(R.drawable.rip)
                        playerView.isClickable = false
                        playerView.isFocusable = false
                    } else {
                        playerView.isClickable = true
                        Glide.with(playAvatar.context).load(currentPlayer.avatar).into(playAvatar)
                    }

                    ready.invisible()
                } else {
                    playerView.isClickable = false
                    Glide.with(playAvatar.context).load(currentPlayer.avatar).into(playAvatar)

                    if (currentPlayer.ready == true) {
                        ready.show()
                    } else {

                        ready.invisible()
                    }
                }


                // show role
                if (currentPlayer == me) {
                    when (currentPlayer.role) {
                        SEER -> {
                            playerRole.show()
                            playerRole.setImageResource(R.drawable.seer)
                        }

                        WITCH -> {
                            playerRole.show()
                            playerRole.setImageResource(R.drawable.witch)
                        }

                        GUARD -> {
                            playerRole.show()
                            playerRole.setImageResource(R.drawable.shield)
                        }

                        VILLAGER -> {
                            playerRole.show()
                            playerRole.setImageResource(R.drawable.villager)
                        }

                        WEREWOLF -> {
                            playerRole.show()
                            playerRole.setImageResource(R.drawable.werewolf_icon)
                        }

                        else -> {
                            playerRole.invisible()
                        }
                    }
                } else {
                    if (me.role == WEREWOLF && currentPlayer.role == WEREWOLF) {
                        playerRole.show()
                        playerRole.setImageResource(R.drawable.werewolf_icon)
                    } else {
                        playerRole.invisible()
                    }

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



