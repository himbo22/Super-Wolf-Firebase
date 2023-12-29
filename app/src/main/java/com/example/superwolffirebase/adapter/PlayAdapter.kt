package com.example.superwolffirebase.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ItemPlayerBinding
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.other.Constant.GUARD
import com.example.superwolffirebase.other.Constant.SEER
import com.example.superwolffirebase.other.Constant.VILLAGER
import com.example.superwolffirebase.other.Constant.WEREWOLF
import com.example.superwolffirebase.other.Constant.WITCH
import com.example.superwolffirebase.other.OnItemClick
import com.example.superwolffirebase.other.PlayDiffUtil
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show


@GlideModule
class PlayAdapter(
    private val id: String,
    private val listener: OnItemClick,
) :
    RecyclerView.Adapter<PlayAdapter.PlayViewHolder>() {

    private var oldPlayerList = emptyList<PlayerInGame>()
    private var me = PlayerInGame()
    private var isDay: Boolean? = false
    private var gameStarted: Boolean? = false
    private var witchPhase: Boolean? = false
    private var harmPower: Boolean? = false
    private var gameEnded: Boolean? = false
    private var seerPicked: Boolean? = false

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

                if (gameEnded == true) {
                    playerRole.show()
                }


                // vote
                if (isDay == true) { // day
                    if (!currentPlayer.dead!!) {
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
                    }

                } else { // night


                    if (witchPhase == true) { // start witch phase
                        if (me.role == WITCH) {
                            if (harmPower == true) {
                                playerView.setOnClickListener {
                                    when (me.voteId) {
                                        "" -> listener.witchPick(currentPlayer)
                                        currentPlayer.id -> listener.witchUnPick(currentPlayer)
                                        else -> listener.witchChangePick(currentPlayer)
                                    }
                                }
                            } else {
                                playerView.setOnClickListener { }

                            }
                        } else {
                            playerView.setOnClickListener { }
                        }

                    } else { // not come to witch phase yet

                        if (me.role == SEER) { // can not expose itself role

                                playerView.setOnClickListener {
                                    when (seerPicked) {
                                        false -> {
                                            if (currentPlayer != me){
                                                listener.seerPick(currentPlayer)
                                            }
                                        }

                                        true -> {

                                        }

                                        null -> {

                                        }
                                    }
                                }


                        } else if (me.role == WEREWOLF) { // can not vote alies

                            if (currentPlayer.role != WEREWOLF && currentPlayer != me) {

                                playerView.setOnClickListener {
                                    when (me.voteId) {
                                        "" -> listener.werewolfVote(currentPlayer)
                                        currentPlayer.id -> listener.werewolfUnVote(
                                            currentPlayer
                                        )

                                        else -> listener.werewolfChangeVote(currentPlayer)
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
                        } else if (me.role == WITCH) {
                            playerView.setOnClickListener {
                            }

                        } else {
                            playerView.setOnClickListener {
                            }
                        }
                    }
                }


                // show vote
                if (isDay == true) { // day
                    toxicPotion.invisible()
                    cover.invisible()

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
                    if (witchPhase == true) {

                        if (me.role == WITCH) {

                            constraintVote.invisible()
                            constraintVoted.invisible()

                            if (me.voteId == currentPlayer.id) {
                                toxicPotion.show()
                            } else {
                                toxicPotion.invisible()
                            }
                        }
                    } else {

                        if (me.role == WEREWOLF) { // if werewolf
                            if (currentPlayer.voteAvatar != null && currentPlayer.role == WEREWOLF){
                                constraintVote.show()
                                Glide.with(avatarVote.context).load(me.voteAvatar)
                                    .into(avatarVote)
                            } else {
                                constraintVote.invisible()
                            }

                            if (currentPlayer.voted != 0) {
                                constraintVoted.show()
                                tvVoted.text = currentPlayer.voted.toString()
                            } else {
                                constraintVoted.invisible()
                            }
                        } else if (me.role == GUARD) {
                            constraintVote.invisible()
                            constraintVoted.invisible()
                            if (me.voteId == currentPlayer.id) {
                                cover.show()
                            } else {
                                cover.invisible()
                            }
                        } else {
                            constraintVote.invisible()
                            constraintVoted.invisible()
                        }


                    }


                }





                if (gameStarted == true) {
                    if (currentPlayer.dead == true) {
                        constraintVote.invisible()
                        constraintVoted.invisible()
                        playAvatar.setImageResource(R.drawable.rip)
                        playerView.isClickable = false
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

    @SuppressLint("NotifyDataSetChanged")
    fun setSeerPicked(newStatus: Boolean) = apply {
        seerPicked = newStatus
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setIsDay(newStatus: Boolean) = apply {
        isDay = newStatus
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setGameStarted(newStatus: Boolean) {
        gameStarted = newStatus
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setWitchPhase(newStatus: Boolean) {
        witchPhase = newStatus
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setHarmPower(newStatus: Boolean) {
        harmPower = newStatus
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setGameEnded(newStatus: Boolean) {
        gameEnded = newStatus
        notifyDataSetChanged()
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



