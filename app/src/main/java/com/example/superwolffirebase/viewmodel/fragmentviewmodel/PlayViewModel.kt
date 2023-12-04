package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.JoinLeaveRoom
import com.example.superwolffirebase.api.SendMessage
import com.example.superwolffirebase.model.MessageRequest
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
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.math.max


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
    private val _prepareToStartGame = MutableLiveData<Resource<String>>()
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
    private val _playerGetKilled = MutableLiveData<Resource<Boolean>>()
    val playerGetKilled get() = _playerGetKilled
    private val _playerGetKilledAtNight = MutableLiveData<Resource<Boolean>>()
    val playerGetKilledAtNight get() = _playerGetKilledAtNight
    private val _seerPick = MutableLiveData<Resource<String>>()
    val seerPick get() = _seerPick
    private val _playerCreateRoom = MutableLiveData<Resource<String>>()
    val playerCreateRoom get() = _playerCreateRoom
    private val _playerBeingTargeted = MutableLiveData<Resource<PlayerInGame>>()
    val playerBeingTargeted get() = _playerBeingTargeted
    private val _harmPowerStatus = MutableLiveData<Resource<Boolean>>()
    val harmPowerStatus get() = _harmPowerStatus
    private val _healPowerStatus = MutableLiveData<Resource<Boolean>>()
    val healPowerStatus get() = _healPowerStatus
    fun seerPick(roomName: String, playerGetExposeRole: PlayerInGame) {
        timer = object : CountDownTimer(3000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _seerPick.postValue(Resource.Success(playerGetExposeRole.role!!))
            }

            override fun onFinish() {
                _seerPick.postValue(Resource.Error(Exception("")))
            }

        }
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


    fun guardPick(roomName: String, playerProtected: PlayerInGame, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${playerProtected.id}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    currentData.child("protected").value = true
                    showLog("da bao ve")
                    firebaseDatabase.getReference("rooms/${roomName}/players/${uid}")
                        .updateChildren(
                            mapOf(
                                "voteAvatar" to playerProtected.avatar,
                                "voteId" to playerProtected.id
                            )
                        )

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (committed) {
                        showLog("hoang cac to 1")
                    } else {
                        showLog("hoang cac to 2")
                    }
                }

            })
    }

    fun guardChangePick(
        roomName: String,
        uid: String,
        newPlayerProtected: PlayerInGame
    ) {
        val reference = firebaseDatabase.reference.child("rooms/${roomName}")
        reference.child("players/${uid}/voteId").get().addOnSuccessListener {
            val oldId = it.value.toString()
            reference.child("players/${oldId}/protected").setValue(false).addOnSuccessListener {
                guardPick(roomName, newPlayerProtected, uid)
            }
        }
    }

    fun guardUnPick(roomName: String, playerProtected: PlayerInGame, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${playerProtected.id}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    currentData.child("protected").value = false
                    showLog("da bao ve")
                    firebaseDatabase.getReference("rooms/${roomName}/players/${uid}")
                        .updateChildren(
                            mapOf(
                                "voteAvatar" to "",
                                "voteId" to ""
                            )
                        )

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


    fun werewolfVote(roomName: String, uid: String, playerVotedId: String, avatar: String) {
        val reference = firebaseDatabase.getReference("rooms/${roomName}")
        reference.child("players/${playerVotedId}").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentVoted = currentData.child("voted").getValue<Int>() ?: 0
                currentData.child("voted").value = currentVoted + 1
                reference.child("players/${uid}").updateChildren(
                    mapOf(
                        "voteAvatar" to avatar,
                        "voteId" to playerVotedId
                    )
                )
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


    fun resetPlayersStatus(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.mapNotNull { it.getValue<PlayerInGame>() }
                            .forEach { player ->
                                firebaseDatabase.getReference("rooms/${roomName}/players/${player.id}")
                                    .updateChildren(
                                        mapOf(
                                            "voteAvatar" to "",
                                            "voteId" to "",
                                            "voted" to 0,
                                            "protected" to false,
                                            "saveByWitch" to false
                                        )
                                    )
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    fun getHarmPower(roomName: String) {
        firebaseDatabase.reference.child("rooms/${roomName}/harmPower").get().addOnSuccessListener {
            val isUsed = it.getValue<Boolean>() ?: false
            _harmPowerStatus.postValue(Resource.Success(isUsed))
        }
    }

    fun getHealPower(roomName: String) {
        firebaseDatabase.reference.child("rooms/${roomName}/healPower").get().addOnSuccessListener {
            val isUsed = it.getValue<Boolean>() ?: false
            _healPowerStatus.postValue(Resource.Success(isUsed))
        }
    }


    fun playerGetKilled(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    firebaseDatabase.reference.child("rooms/${roomName}/remaining").get()
                        .addOnSuccessListener { remainingSnapshot ->
                            val remaining =
                                remainingSnapshot.getValue<Int>() ?: return@addOnSuccessListener
                            val voteThreshold =
                                if (remaining % 3 == 0) remaining / 3 else remaining / 3 + 1
                            if (snapshot.exists()) {
                                snapshot.children.mapNotNull { it.getValue<PlayerInGame>() }
                                    .filter { it.voted!! >= voteThreshold }
                                    .maxByOrNull { it.voted!! }
                                    ?.takeIf { it.voted!! >= voteThreshold }
                                    ?.let {
                                        firebaseDatabase.getReference("rooms/${roomName}/players/${it.id}/dead")
                                            .setValue(true)
                                        resetPlayersStatus(roomName)
                                    }

                            }
                        }


                }

                override fun onCancelled(error: DatabaseError) {
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

                }

            })
    }

    fun autoPickRole(roomName: String, uid: String) {
        val roles = arrayListOf(
//            WEREWOLF,
            VILLAGER,
//            SEER,
//            WITCH,
            GUARD
        )
        val reference = firebaseDatabase.getReference("rooms/${roomName}")
        reference.child("gameStarted").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue<Boolean>() == true) {
                    reference.child("players")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var myRole = ""
                                    for (playerInSide in snapshot.children) {
                                        val player = playerInSide.getValue<PlayerInGame>()

                                        if (player?.role.isNullOrBlank() && player?.id == uid) {
                                            val role = roles.random()
                                            myRole = role
                                            roles.remove(role)
                                            reference.child("players/${player.id}/role")
                                                .setValue(role)
                                        } else {
                                            val role = roles.random()
                                            roles.remove(role)
                                            reference.child("players/${player!!.id}/role")
                                                .setValue(role)
                                        }

                                    }
                                    _role.postValue(Event(Resource.Success(myRole)))
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                _role.postValue(Event(Resource.Error(error.toException())))
                            }

                        })

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    // room
    fun prepareToStartGame(roomName: String) {
        val reference = firebaseDatabase.getReference("rooms/${roomName}/players")
        reference.addValueEventListener(object : ValueEventListener {
            var ready = false
            override fun onDataChange(snapshot: DataSnapshot) {
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

                        timer = object : CountDownTimer(3000L, 1000L) {
                            override fun onTick(millisUntilFinished: Long) {


                            }

                            override fun onFinish() {
                                firebaseDatabase.getReference("rooms/${roomName}/gameStarted")
                                    .setValue(true)
                            }

                        }.start()

                        reference.removeEventListener(this)

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


    fun startNewDay(roomName: String, uid: String) {



        timer = object : CountDownTimer(10000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timeCountdown.postValue(Resource.Success((millisUntilFinished / 1000).toString()))
            }

            override fun onFinish() {
                updateRoomDayAndNight(roomName, uid, true)
                playerGetKilled(roomName)
                startNewNight(roomName, uid)
            }
        }.start()


    }

    fun startWitchPhase(roomName: String, uid: String) {

        firebaseDatabase.getReference("rooms/${roomName}/witchPhase").setValue(true)

        firebaseDatabase.getReference("rooms/${roomName}/players")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val isWitch = snapshot.child("$uid/role").getValue<String>()
                        val maxPlayerVoted =
                            snapshot.children.mapNotNull { it.getValue<PlayerInGame>() }
                                .maxByOrNull { player ->
                                    player.voted!!
                                }
                        val result = if (maxPlayerVoted != null &&
                            snapshot.children.count {
                                it.getValue<PlayerInGame>()?.voted == maxPlayerVoted.voted
                            } > 1
                        ) {
                            null
                        } else {
                            maxPlayerVoted
                        }

                        if (result != null) { // there is one player being targeted
                            timer = object : CountDownTimer(15000L, 1000L) {
                                override fun onTick(millisUntilFinished: Long) {
                                    _timeCountdown.postValue(Resource.Success((millisUntilFinished / 1000).toString()))
                                    if (isWitch == WITCH){
                                        _playerBeingTargeted.postValue(Resource.Success(maxPlayerVoted!!))
                                    }
                                }

                                override fun onFinish() {
                                    if (isWitch == WITCH){
                                        _playerBeingTargeted.postValue(Resource.Error(Exception("")))
                                    }

                                    if (maxPlayerVoted?.savedByWitch == true) {
                                        resetPlayersStatus(roomName)
                                        startNewDay(roomName, uid)
                                        updateRoomDayAndNight(roomName, uid, false)
                                    } else {
                                        if (maxPlayerVoted?.protected == true) {
                                            resetPlayersStatus(roomName)
                                            startNewDay(roomName, uid)
                                            updateRoomDayAndNight(roomName, uid, false)
                                        } else {
//                                            firebaseDatabase.getReference("rooms/${roomName}/players/${maxPlayerVoted?.id}/dead")
//                                                .setValue(true)
                                            resetPlayersStatus(roomName)
                                            startNewDay(roomName, uid)
                                            updateRoomDayAndNight(roomName, uid, false)
                                        }
                                    }
                                }

                            }.start()

                        } else { // there no one player being target

                            timer = object : CountDownTimer(15000L, 1000L) {
                                override fun onTick(millisUntilFinished: Long) {
                                    _timeCountdown.postValue(Resource.Success((millisUntilFinished / 1000).toString()))
                                    if (isWitch == WITCH){
                                        _playerBeingTargeted.postValue(Resource.Success(maxPlayerVoted!!))

                                    }
                                }

                                override fun onFinish() {
                                    if (isWitch == WITCH){

                                        _playerBeingTargeted.postValue(Resource.Error(Exception("")))
                                    }
                                    resetPlayersStatus(roomName)
                                    startNewDay(roomName, uid)
                                    updateRoomDayAndNight(roomName, uid, false)
                                }

                            }.start()
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }


    fun startNewNight(roomName: String, uid: String) {


        timer = object : CountDownTimer(30000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {

                _timeCountdown.postValue(Resource.Success((millisUntilFinished / 1000).toString()))
            }

            override fun onFinish() {


                startWitchPhase(roomName, uid)


            }
        }.start()

    }


    fun updateRoomDayAndNight(roomName: String, uid: String, day: Boolean) {
        val reference = firebaseDatabase.getReference("rooms/${roomName}")
        reference.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val playerCreateRoomId =
                    currentData.child("playerCreateRoomId").getValue<String>() ?: ""
                if (uid == playerCreateRoomId) {
                    if (day) {
                        val currentDay = currentData.child("days").getValue<Int>() ?: 0
                        currentData.child("days").value = currentDay + 1
                    } else {
                        val currentNight = currentData.child("nights").getValue<Int>() ?: 0
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
                if (day) {
                    reference.child("day").setValue(false)
                } else {
                    reference.child("witchPhase").setValue(false)
                    reference.child("day").setValue(true)
                }
            }

        })
    }

    fun startGame(roomName: String, uid: String) {
        val reference = firebaseDatabase.getReference("rooms/${roomName}/gameStarted")


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val started = snapshot.getValue(Boolean::class.java) ?: false
                if (started) {
                    startNewDay(roomName, uid)
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

                }

            })
    }


    fun playerCreateRoom(roomName: String, oldPlayerId: String) {
        val reference = firebaseDatabase.getReference("rooms/${roomName}/players/${oldPlayerId}")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    firebaseDatabase.reference.child("rooms/${roomName}")
                        .get().addOnSuccessListener {
                            val id = it.child("playerCreateRoomId").getValue<String>() ?: ""
                            _playerCreateRoom.postValue(Resource.Success(id))
                        }
                } else {
                    val ref = firebaseDatabase.getReference("rooms/${roomName}")

                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val gameStart =
                                snapshot.child("gameStarted").getValue<Boolean>() ?: false
                            if (gameStart) {
                                val id =
                                    snapshot.child("players").children.mapNotNull { it.getValue<PlayerInGame>() }[0].id
                                        ?: ""
                                firebaseDatabase.getReference("rooms/${roomName}/playerCreateRoomId")
                                    .setValue(id)
                                _playerCreateRoom.postValue(Resource.Success(id))
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            _playerCreateRoom.postValue(Resource.Error(error.toException()))
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _playerCreateRoom.postValue(Resource.Error(error.toException()))
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


    fun leaveRoom(name: String, id: String) = viewModelScope.launch {
        _leaveRoomResult.postValue(Event(Resource.Loading))
        val result = leaveRoom.leaveRoom(name, id)
        _leaveRoomResult.postValue(result)
    }





}