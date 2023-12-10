package com.example.superwolffirebase.other

import com.example.superwolffirebase.model.PlayerInGame

interface OnItemClick {
    fun vote(currentPlayer: PlayerInGame)
    fun unVote(currentPlayer: PlayerInGame)
    fun changeVote(currentPlayer: PlayerInGame)

    fun seerPick(currentPlayer: PlayerInGame)

    fun werewolfVote(currentPlayer: PlayerInGame)

    fun werewolfUnVote(currentPlayer: PlayerInGame)

    fun werewolfChangeVote(currentPlayer: PlayerInGame)

    fun guardVote(currentPlayer: PlayerInGame)

    fun guardUnVote(currentPlayer: PlayerInGame)

    fun guardChangeVote(currentPlayer: PlayerInGame)


    fun witchPick(currentPlayer: PlayerInGame)

    fun witchUnPick(currentPlayer: PlayerInGame)

    fun witchChangePick(currentPlayer: PlayerInGame)
}