package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.JoinLeaveRoom
import com.example.superwolffirebase.api.SendMessage
import com.example.superwolffirebase.model.MessageRequest
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.showLog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.annotation.Nullable
import javax.inject.Inject


@HiltViewModel
class PlayViewModel @Inject constructor(
    private val leaveRoom: JoinLeaveRoom,
    private val firebaseDatabase: FirebaseDatabase,
    private val repository: SendMessage
) : ViewModel() {

    private val _leaveRoomResult = MutableLiveData<Event<Resource<DatabaseReference>>>()
    val leaveRoomResult get() = _leaveRoomResult
    private val _allPlayer = MutableLiveData<Event<Resource<List<PlayerInGame>>>>()
    val allPlayer get() = _allPlayer
    private val _sendMessResult = MutableLiveData<Resource<String>>()
    val sendMessResult get() = _sendMessResult
    private val _allMessage = MutableLiveData<Event<Resource<List<MessageRequest>>>>()
    val allMessage get() = _allMessage
    private val _votePlayer = MutableLiveData<Event<Resource<Void>>>()
    val votePlayer get() = _votePlayer
    private val _getPlayerInGame = MutableLiveData<Event<Resource<PlayerInGame>>>()
    val getPlayerInGame get() = _getPlayerInGame


    fun getPlayerInGame(id: String, roomName: String){
        firebaseDatabase.getReference("rooms/${roomName}/players/${id}").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val playerInGame = snapshot.getValue<PlayerInGame>()
                    if (playerInGame != null){
                        _getPlayerInGame.postValue(Event(Resource.Success(playerInGame)))
                    } else {
                        _getPlayerInGame.postValue(Event(Resource.Error(Exception("No data"))))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun sendMessage(
        roomName: String,
        message: String,
        playerName: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        val result = repository.sendMessage(roomName, message, playerName)
        _sendMessResult.postValue(result)
    }

    fun votePlayer(avatar: String, roomName: String, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${uid}").updateChildren(
            mapOf(
                "vote" to avatar
            )
        ).addOnSuccessListener {
            _votePlayer.postValue(Event(Resource.Success(it)))
        }.addOnFailureListener {
            _votePlayer.postValue(Event(Resource.Error(Exception("ASD"))))
        }
    }

    fun getAllMessages(roomName: String) {
        firebaseDatabase.getReference("messages/${roomName}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = arrayListOf<MessageRequest>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            val message = item.getValue(MessageRequest::class.java)
                            if (message != null) {
                                messageList.add(message)
                            }
                        }
                        _allMessage.postValue(Event(Resource.Success(messageList)))
                    } else {
                        _allMessage.postValue(Event(Resource.Error(Exception("Error"))))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }


    fun getAllPlayers(name: String) {
        firebaseDatabase.getReference("rooms/${name}/players")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val playerList = arrayListOf<PlayerInGame>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            val player = item.getValue(PlayerInGame::class.java)
                            if (player != null) {
                                playerList.add(player)
                            }
                        }
                        _allPlayer.postValue(Event(Resource.Success(playerList)))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _allPlayer.postValue(Event(Resource.Error(Exception("qwd"))))
                }

            })
    }


    fun leaveRoom(name: String, amount: Int, id: String) = viewModelScope.launch {
        _leaveRoomResult.postValue(Event(Resource.Loading))
        val result = leaveRoom.leaveRoom(name, amount, id)
        _leaveRoomResult.postValue(result)
    }


}