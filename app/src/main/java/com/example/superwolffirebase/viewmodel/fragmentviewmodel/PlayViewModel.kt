package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.JoinLeaveRoom
import com.example.superwolffirebase.api.SendMessage
import com.example.superwolffirebase.model.MessageRequest
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Constant.WEREWOLF
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
import java.util.Date
import javax.annotation.Nullable
import javax.inject.Inject
import kotlin.concurrent.timer


@HiltViewModel
class PlayViewModel @Inject constructor(
    private val leaveRoom: JoinLeaveRoom,
    private val firebaseDatabase: FirebaseDatabase,
    private val repository: SendMessage
) : ViewModel() {

    private lateinit var timer: CountDownTimer


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
    private val _readyStatus = MutableLiveData<Resource<Boolean>>()
    val readyStatus get() = _readyStatus
    private val _getRoom = MutableLiveData<Event<Resource<Room>>>()
    val getRoom get() = _getRoom
    private val _prepareToStartGame = MutableLiveData<Resource<Boolean>>()
    val prepareToStartGame get() = _prepareToStartGame
    private val _timeCountdown = MutableLiveData<Resource<String>>()
    val timeCountDown get() = _timeCountdown
    private val _allNights = MutableLiveData<Resource<String>>()
    val allNights get() = _allNights
    private val _allDays = MutableLiveData<Resource<String>>()
    val allDays get() = _allDays
    private val _startGame = MutableLiveData<Event<Resource<Boolean>>>()
    val startGame get() = _startGame



    fun playingGame(roomName: String){

    }


    fun startGame(roomName: String){
        firebaseDatabase.getReference("rooms/${roomName}").runTransaction(object : Transaction.Handler{
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                currentData.child("gameStarted").value = true
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                TODO("Not yet implemented")
            }

        })
    }





    fun ready(roomName: String, id: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${id}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val ready = currentData.child("ready").getValue(Boolean::class.java)
                    return if (ready == true) {
                        currentData.child("ready").value = false
                        Transaction.success(currentData)
                    } else {
                        currentData.child("ready").value = true
                        Transaction.success(currentData)
                    }
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (committed && error == null) {
                        val currentReady = currentData?.child("ready")?.getValue(Boolean::class.java)
                        _readyStatus.postValue(Resource.Success(currentReady!!))
                    } else {
                        _readyStatus.postValue(Resource.Error(error!!.toException()))
                    }
                }

            })
    }




    fun prepareToStartGame(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var ready : Boolean = false 
                if (snapshot.exists()){
                    for (snap in snapshot.children){
                        val playerInGame = snap.getValue<PlayerInGame>()
                        if (playerInGame?.ready == false){
                            ready = false
                            break
                        } else {
                            ready = true
                        }
                    }
                    if (ready){
                        _prepareToStartGame.postValue(Resource.Success(true))
                    } else {
                        _prepareToStartGame.postValue(Resource.Error(Exception("Error")))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _prepareToStartGame.postValue(Resource.Error(Exception("Error")))
            }

        })


    }


    fun getRoom(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val room = snapshot.getValue<Room>()
                        if (room != null){
                            _getRoom.postValue(Event(Resource.Success(room)))
                        } else {
                            _getRoom.postValue(Event(Resource.Error(Exception("Error"))))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _getRoom.postValue(Event(Resource.Error(error.toException())))
                }

            })
    }




    fun timeRemaining(time: Long) {
        timer = object : CountDownTimer(time, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timeCountdown.postValue(Resource.Success(((millisUntilFinished / 1000)).toString()))
            }

            override fun onFinish() {
                _timeCountdown.postValue(Resource.Error(java.lang.Exception("Yes sir")))
            }
        }.start()
    }


    fun getPlayerInGame(id: String, roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${id}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val playerInGame = snapshot.getValue<PlayerInGame>()
                        if (playerInGame != null) {
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

    fun votePlayer(avatar: String, roomName: String, uid: String, playerVotedId: String) {

        firebaseDatabase.getReference("rooms/${roomName}/players/${playerVotedId}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentVoted = currentData.child("voted").getValue(Int::class.java)
                    if (currentVoted != null) {
                        currentData.child("voted").value = currentVoted + 1
                    }
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (committed && error == null) {
                        firebaseDatabase.getReference("rooms/${roomName}/players/${uid}")
                            .updateChildren(
                                mapOf(
                                    "vote" to avatar
                                )
                            ).addOnSuccessListener {
                                _votePlayer.postValue(Event(Resource.Success(it)))
                            }
                    } else {
                        _votePlayer.postValue(Event(Resource.Error(Exception("ASD"))))

                    }
                }
            })

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


    fun getAllPlayers(name: String, id: String) {
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
                        val me = playerList.first {
                            it.id == id
                        }
                        _allPlayer.postValue(Event(Resource.Success(playerList.map {

                            if ((it.role == WEREWOLF && me.role == WEREWOLF) || (me.role == WEREWOLF && it.id == me.id)) {
                                it.copy(
                                    isShowRole = true
                                )
                            } else {
                                it
                            }

                        })))
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