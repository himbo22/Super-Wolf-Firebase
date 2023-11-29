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
import com.example.superwolffirebase.other.Constant.GUARD
import com.example.superwolffirebase.other.Constant.SEER
import com.example.superwolffirebase.other.Constant.VILLAGER
import com.example.superwolffirebase.other.Constant.WEREWOLF
import com.example.superwolffirebase.other.Constant.WITCH
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.showLog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.values
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
    private val _votePlayer = MutableLiveData<Resource<Int>>()
    val votePlayer get() = _votePlayer
    private val _unVotePlayer = MutableLiveData<Resource<Int>>()
    val unVotePlayer get() = _unVotePlayer
    private val _getPlayerInGame = MutableLiveData<Resource<PlayerInGame>>()
    val getPlayerInGame get() = _getPlayerInGame
    private val _readyStatus = MutableLiveData<Resource<Boolean>>()
    val readyStatus get() = _readyStatus
    private val _getRoom = MutableLiveData<Event<Resource<Room>>>()
    val getRoom get() = _getRoom
    private val _prepareToStartGame = MutableLiveData<Resource<Boolean>>()
    val prepareToStartGame get() = _prepareToStartGame
    private val _timeCountdown = MutableLiveData<Resource<String>>()
    val timeCountDown get() = _timeCountdown
    private val _startGame = MutableLiveData<Resource<Boolean>>()
    val startGame get() = _startGame
    private val _role = MutableLiveData<Event<Resource<String>>>()
    val role get() = _role
    private val _startNewDay = MutableLiveData<Resource<String>>()
    val startNewDay get() = _startNewDay
    private val _startNewNight = MutableLiveData<Resource<String>>()
    val startNewNight get() = _startNewNight


    fun playerGetKilled(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    firebaseDatabase.reference.child("rooms/${roomName}/remaining").get()
                        .addOnSuccessListener {
                            val remaining = it.getValue<Int>()
                            showLog(remaining.toString())
                            var (a, b) = Pair(0, "")
                            if (snapshot.exists()) {
                                for (snap in snapshot.children) {
                                    val player = snap.getValue<PlayerInGame>()
                                    if (player != null && remaining != null) {
                                        if (remaining % 3 == 0) {
                                            if (player.voted!! >= (remaining / 3) && player.voted!! >= a) {
                                                a = player.voted!!
                                                b = player.id!!
                                            }
                                        } else {
                                            if (player.voted!! >= (remaining / 3 + 1)) {
                                                a = player.voted!!
                                                b = player.id!!
                                            }
                                        }
                                    }
                                }


                                if (remaining!! % 3 == 0) {
                                    if (a >= remaining / 3) {
                                        firebaseDatabase.getReference("rooms/${roomName}/players/${b}/dead")
                                            .setValue(true)
                                    }
                                } else {
                                    if (a >= remaining / 3 + 1) {
                                        firebaseDatabase.getReference("rooms/${roomName}/players/${b}/dead")
                                            .setValue(true)
                                    }
                                }
                            }
                        }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun playersRemaining(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var remaining = 0
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val player = snap.getValue<PlayerInGame>()
                            if (player?.dead == false) {
                                remaining += 1
                            }
                        }
                        firebaseDatabase.getReference("rooms/${roomName}/remaining")
                            .setValue(remaining)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun autoPickRole(roomName: String) {
        val roles = arrayListOf(
            WEREWOLF, VILLAGER, SEER, WITCH, GUARD
        )
        val reference = firebaseDatabase.getReference("rooms/${roomName}/players")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (playerInSide in snapshot.children) {
                        val player = playerInSide.getValue<PlayerInGame>()

                        if (player?.role.isNullOrBlank()) {
                            val role = roles.random()
                            roles.remove(role)
                            reference.child("${player!!.id}/role").setValue(role)
                        }

                    }
                    _role.postValue(Event(Resource.Success("")))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _role.postValue(Event(Resource.Error(error.toException())))
            }

        })
    }


    fun startNewDay(roomName: String) {
        timer = object : CountDownTimer(10000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timeCountdown.postValue(Resource.Success((millisUntilFinished / 1000).toString()))
            }

            override fun onFinish() {
                updateDays(roomName, true)
                playerGetKilled(roomName)
                startNewNight(roomName)
                _startNewDay.postValue(Resource.Success("day sir"))
            }
        }.start()
    }

    fun startNewNight(roomName: String) {
        timer = object : CountDownTimer(10000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {

                _timeCountdown.postValue(Resource.Success((millisUntilFinished / 1000).toString()))
            }

            override fun onFinish() {
                updateDays(roomName, false)
                startNewDay(roomName)
                _startNewNight.postValue(Resource.Success("yes night"))
            }
        }.start()
    }


    fun updateDays(roomName: String, day: Boolean) {

        firebaseDatabase.getReference("rooms/${roomName}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    if (day) {
                        val currentDay = currentData.child("days").getValue<Int>()
                        if (currentDay != null) {
                            currentData.child("days").value = currentDay + 1
                        }
                    } else {
                        val currentNight = currentData.child("nights").getValue<Int>()
                        if (currentNight != null) {
                            currentData.child("nights").value = currentNight + 1
                        }
                    }

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                }

            })
    }


    fun startGame(roomName: String) {
        val reference = firebaseDatabase.getReference("rooms/${roomName}/gameStarted")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val started = snapshot.getValue(Boolean::class.java) ?: false
                if (started) {
                    startNewDay(roomName)
                    reference.removeEventListener(this)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }


    // player
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
                        val currentReady =
                            currentData?.child("ready")?.getValue(Boolean::class.java)
                        _readyStatus.postValue(Resource.Success(currentReady!!))
                    } else {
                        _readyStatus.postValue(Resource.Error(error!!.toException()))
                    }
                }

            })
    }


    // room
    fun prepareToStartGame(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var ready: Boolean = false
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val playerInGame = snap.getValue<PlayerInGame>()
                            if (playerInGame?.ready == false) {
                                ready = false
                                break
                            } else {
                                ready = true
                            }
                        }
                        if (ready) {
                            firebaseDatabase.getReference("rooms/${roomName}/gameStarted")
                                .setValue(true).addOnSuccessListener {
                                    _prepareToStartGame.postValue(Resource.Success(true))
                                }

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
                        if (room != null) {
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


    fun getPlayerInGame(id: String, roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${id}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val playerInGame = snapshot.getValue<PlayerInGame>()
                        if (playerInGame != null) {
                            _getPlayerInGame.postValue((Resource.Success(playerInGame)))
                        } else {
                            _getPlayerInGame.postValue((Resource.Error(Exception("No data"))))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _getPlayerInGame.postValue((Resource.Error(Exception("No data"))))
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

    fun changeVotePlayer(
        roomName: String,
        uid: String,
        playerVotedId: String,
        playerVoteAvatar: String
    ) {
        val reference = firebaseDatabase.getReference("rooms/${roomName}/players")



        firebaseDatabase.reference.child("rooms/${roomName}/players/${uid}/voteId").get()
            .addOnSuccessListener {
                val oldId = it.value.toString()
                reference.child(oldId).runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentVote = currentData.child("voted").getValue<Int>()
                        if (currentVote != null) {
                            currentData.child("voted").value = currentVote - 1
                        }
                        votePlayer(playerVoteAvatar, roomName, uid, playerVotedId)
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {

                    }

                })
            }


    }

    fun unVotePlayer(roomName: String, uid: String, playerVotedId: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${playerVotedId}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentVoted = currentData.child("voted").getValue(Int::class.java)
                    if (currentVoted != null) {
                        currentData.child("voted").value = currentVoted - 1
                        firebaseDatabase.getReference("rooms/${roomName}/players/${uid}")
                            .updateChildren(
                                mapOf(
                                    "voteAvatar" to "",
                                    "voteId" to ""
                                )
                            )
                    }
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {

                }

            })
    }

    fun votePlayer(avatar: String, roomName: String, uid: String, playerVotedId: String) {

        firebaseDatabase.getReference("rooms/${roomName}/players/${playerVotedId}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentVoted = currentData.child("voted").getValue(Int::class.java)
                    if (currentVoted != null) {
                        currentData.child("voted").value = currentVoted + 1
                        firebaseDatabase.getReference("rooms/${roomName}/players/${uid}")
                            .updateChildren(
                                mapOf(
                                    "voteAvatar" to avatar,
                                    "voteId" to playerVotedId
                                )
                            )
                    }

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {

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
                    _allMessage.postValue(Event(Resource.Error(error.toException())))

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