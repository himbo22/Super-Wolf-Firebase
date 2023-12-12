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
import com.google.firebase.database.values
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
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
    private val _unVotePlayer = MutableLiveData<Resource<Int>>()
    val unVotePlayer get() = _unVotePlayer
    private val _getPlayerInGame = MutableLiveData<Resource<PlayerInGame>>()
    val getPlayerInGame get() = _getPlayerInGame
    private val _getRoom = MutableLiveData<Event<Resource<Room>>>()
    val getRoom = _getRoom
    private val _timeCountdown = MutableLiveData<Resource<String>>()
    val timeCountDown get() = _timeCountdown

    private val _role = MutableLiveData<Event<Resource<String>>>()
    val role get() = _role
    private val _startNewDay = MutableLiveData<Resource<String>>()
    val startNewDay get() = _startNewDay
    private val _startNewNight = MutableLiveData<Resource<String>>()
    val startNewNight get() = _startNewNight
    private val _seerPick = MutableLiveData<Resource<PlayerInGame>>()
    val seerPick get() = _seerPick
    private val _playerCreateRoom = MutableLiveData<Resource<String>>()
    val playerCreateRoom get() = _playerCreateRoom
    private val _playerBeingTargeted = MutableLiveData<Resource<PlayerInGame>>()
    val playerBeingTargeted get() = _playerBeingTargeted
    private val _harmPowerStatus = MutableLiveData<Resource<Boolean>>()
    val harmPowerStatus get() = _harmPowerStatus
    private val _healPowerStatus = MutableLiveData<Resource<Boolean>>()
    val healPowerStatus get() = _healPowerStatus
    private val _witchSaveStatus = MutableLiveData<Resource<Boolean>>()
    val witchSaveStatus get() = _witchSaveStatus
    private val _gameStartedStatus = MutableLiveData<Resource<Boolean>>()
    val gameStartedStatus get() = _gameStartedStatus
    private val _witchPhaseStatus = MutableLiveData<Resource<Boolean>>()
    val witchPhaseStatus get() = _witchPhaseStatus
    private val _gameEndedStatus = MutableLiveData<Resource<Boolean>>()
    val gameEndedStatus get() = _gameEndedStatus
    private val _roomIsDayStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val roomIsDayStatus get() = _roomIsDayStatus
    private val _roomSeerPickedStatus = MutableLiveData<Resource<Boolean>>()
    val roomSeerPickedStatus = _roomSeerPickedStatus

    fun seerPick(playerGetExposeRole: PlayerInGame, roomName: String) {

        firebaseDatabase.getReference("rooms/${roomName}/seerPicked").setValue(true)
            .addOnSuccessListener {
                firebaseDatabase.getReference("rooms/${roomName}/witchPhase")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val witchPhase = snapshot.getValue<Boolean>() ?: false
                                if (witchPhase) {
                                    _seerPick.postValue(Resource.Success(PlayerInGame()))
                                    snapshot.ref.removeEventListener(this)
                                } else {
                                    _seerPick.postValue(Resource.Success(playerGetExposeRole))
                                }
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            }


    }


    fun checkWitchKilled(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.child("players").children.mapNotNull { it.getValue<PlayerInGame>() }
                            .firstOrNull { it.killByWitch == true }
                            ?.let { player ->
                                firebaseDatabase.getReference("rooms/${roomName}/players/${player.id}/dead")
                                    .setValue(true)
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun witchKill(roomName: String, playerWantKill: PlayerInGame, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${playerWantKill.id}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    currentData.child("killByWitch").value = true
                    firebaseDatabase.getReference("rooms/${roomName}/players/${uid}")
                        .updateChildren(
                            mapOf(
                                "voteId" to playerWantKill.id
                            )
                        )
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
                ) {
                }
            })
    }

    fun witchUnKill(roomName: String, playerWantKill: PlayerInGame, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${playerWantKill.id}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    currentData.child("killByWitch").value = false
                    firebaseDatabase.getReference("rooms/${roomName}/players/${uid}")
                        .updateChildren(
                            mapOf(
                                "voteId" to ""
                            )
                        )
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
                ) {
                }
            })
    }

    fun witchChangeKill(roomName: String, playerWantKill: PlayerInGame, uid: String) {
        val reference = firebaseDatabase.reference.child("rooms/${roomName}")
        reference.child("players/${uid}/voteId").get().addOnSuccessListener {
            val oldId = it.value.toString()
            reference.child("players/${oldId}/killByWitch").setValue(false).addOnSuccessListener {
                witchKill(roomName, playerWantKill, uid)
            }
        }
    }


    fun changeVotePlayer(
        roomName: String, uid: String, playerVotedId: String, playerVoteAvatar: String
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
                        error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
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
                                    "voteAvatar" to "", "voteId" to ""
                                )
                            )
                    }
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
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
                                    "voteAvatar" to avatar, "voteId" to playerVotedId
                                )
                            )
                    }

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
                ) {

                }
            })

    }


    fun guardPick(roomName: String, playerProtected: PlayerInGame, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players/${playerProtected.id}")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    currentData.child("protected").value = true
                    firebaseDatabase.getReference("rooms/${roomName}/players/${uid}")
                        .updateChildren(
                            mapOf(
                                "voteId" to playerProtected.id
                            )
                        )

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
                ) {

                }

            })
    }

    fun guardChangePick(
        roomName: String, uid: String, newPlayerProtected: PlayerInGame
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
                    firebaseDatabase.getReference("rooms/${roomName}/players/${uid}")
                        .updateChildren(
                            mapOf(
                                "voteId" to ""
                            )
                        )

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
                ) {

                }

            })
    }


    fun witchSave(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

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

                    if (result != null) {
                        firebaseDatabase.getReference("rooms/${roomName}/players/${maxPlayerVoted!!.id}/saveByWitch")
                            .runTransaction(object : Transaction.Handler {
                                override fun doTransaction(currentData: MutableData): Transaction.Result {
                                    val saved = currentData.getValue<Boolean>()
                                    return if (saved == false) {
                                        currentData.value = true
                                        _witchSaveStatus.postValue(Resource.Success(true))
                                        Transaction.success(currentData)
                                    } else {
                                        currentData.value = false
                                        _witchSaveStatus.postValue(Resource.Success(false))
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

                }

                override fun onCancelled(error: DatabaseError) {
                    _witchSaveStatus.postValue(Resource.Error(error.toException()))
                }

            })
    }


    fun resetPlayersStatus(roomName: String, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        firebaseDatabase.reference.child("rooms/${roomName}/playerCreateRoomId")
                            .get().addOnSuccessListener { playerCreate ->
                                val playerCreateRoom = playerCreate.getValue<String>() ?: ""
                                if (playerCreateRoom == uid) {
                                    snapshot.children.mapNotNull { it.getValue<PlayerInGame>() }
                                        .forEach { player ->
                                            firebaseDatabase.getReference("rooms/${roomName}/players/${player.id}")
                                                .updateChildren(
                                                    mapOf(
                                                        "voteAvatar" to "",
                                                        "voteId" to "",
                                                        "voted" to 0,
                                                        "protected" to false,
                                                        "killByWitch" to false
                                                    )
                                                )
                                        }
                                }
                            }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }


    fun getHealPower(roomName: String, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val player = snapshot.child("players/${uid}").getValue<PlayerInGame>()
                        if (player?.role == WITCH) {
                            val isUsed = snapshot.child("healPower").getValue<Boolean>() ?: false
                            _healPowerStatus.postValue(Resource.Success(isUsed))
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _healPowerStatus.postValue(Resource.Error(error.toException()))
                }

            })
    }


    fun playerGetKilled(roomName: String, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}/players")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    firebaseDatabase.reference.child("rooms/${roomName}/remaining").get()
                        .addOnSuccessListener { remainingSnapshot ->
                            if (snapshot.exists()) {
                                firebaseDatabase.reference.child("rooms/${roomName}/playerCreateRoomId")
                                    .get().addOnSuccessListener { playerCreateRoom ->
                                        val playerCreate = playerCreateRoom.getValue<String>() ?: ""
                                        if (playerCreate == uid) {
                                            val remaining = remainingSnapshot.getValue<Int>() ?: 0

                                            val voteThreshold =
                                                if (remaining % 3 == 0) remaining / 3 else remaining / 3 + 1

                                            snapshot.children.mapNotNull { it.getValue<PlayerInGame>() }
                                                .filter { it.voted!! >= voteThreshold }
                                                .maxByOrNull { it.voted!! }
                                                ?.takeIf { it.voted!! >= voteThreshold }
                                                ?.let { player ->

                                                    val result = if (snapshot.children.count {
                                                            it.getValue<PlayerInGame>()?.voted == player.voted
                                                        } > 1) {
                                                        null
                                                    } else {
                                                        player
                                                    }

                                                    if (result != null) {
                                                        firebaseDatabase.getReference("rooms/${roomName}/players/${result.id}/dead")
                                                            .setValue(true)
                                                        resetPlayersStatus(roomName, uid)
                                                    } else {
                                                        resetPlayersStatus(roomName, uid)
                                                    }

                                                }
                                        }
                                    }

                            }
                        }


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    fun playersRemaining(roomName: String, uid: String) {
        firebaseDatabase.getReference("rooms/${roomName}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val playerCreateRoom =
                            snapshot.child("playerCreateRoomId").getValue<String>()
                        if (playerCreateRoom == uid) {
                            val remaining = snapshot.child("players").children.count {
                                it.getValue<PlayerInGame>()?.dead == false
                            }
                            firebaseDatabase.getReference("rooms/${roomName}/remaining")
                                .setValue(remaining)
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun autoPickRole(roomName: String, uid: String) {
        val roles = arrayListOf(
//            WEREWOLF,
//            WEREWOLF,
//            WEREWOLF,
//            VILLAGER,
//            VILLAGER,
//            VILLAGER,
//            SEER,
            WITCH,
//            GUARD
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

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }


    fun startNewDay(roomName: String, uid: String) {


        timer = object : CountDownTimer(10000L, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                _timeCountdown.postValue(Resource.Success((millisUntilFinished / 1000).toString()))
                _startNewDay.postValue(Resource.Success(""))
            }

            override fun onFinish() {
                _startNewDay.postValue(Resource.Error(Exception()))
                updateRoomDayAndNight(roomName, uid, true)
                playerGetKilled(roomName, uid)
                startNewNight(roomName, uid)
            }
        }.start()


    }


    fun startWitchPhase(roomName: String, uid: String) {

        firebaseDatabase.getReference("rooms/${roomName}").updateChildren(
            mapOf(
                "witchPhase" to true, "seerPicked" to false
            )
        )


        firebaseDatabase.getReference("rooms/${roomName}/playerCreateRoomId")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val playerCreateRoom = snapshot.getValue<String>()
                        if (playerCreateRoom == uid) {
                            firebaseDatabase.getReference("rooms/${roomName}/players")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {

                                            val isWitch =
                                                snapshot.child("$uid/role").getValue<String>()

                                            val maxPlayerVoted =
                                                snapshot.children.mapNotNull { it.getValue<PlayerInGame>() }
                                                    .maxByOrNull { player ->
                                                        player.voted!!
                                                    }

                                            val result =
                                                if (maxPlayerVoted != null && snapshot.children.count {
                                                        it.getValue<PlayerInGame>()?.voted == maxPlayerVoted.voted
                                                    } > 1) {
                                                    null
                                                } else {
                                                    maxPlayerVoted
                                                }


                                            if (result != null) { // there is one player being targeted
                                                timer = object : CountDownTimer(10000L, 1000L) {
                                                    override fun onTick(millisUntilFinished: Long) {
                                                        _timeCountdown.postValue(Resource.Success((millisUntilFinished / 1000).toString()))
                                                        if (isWitch == WITCH) {
                                                            _playerBeingTargeted.postValue(
                                                                Resource.Success(
                                                                    maxPlayerVoted!!
                                                                )
                                                            )
                                                        }

                                                    }

                                                    override fun onFinish() {

                                                        checkWitchKilled(roomName)

                                                        if (isWitch == WITCH) {
                                                            _playerBeingTargeted.postValue(
                                                                Resource.Error(
                                                                    Exception("")
                                                                )
                                                            )
                                                        }

                                                        firebaseDatabase.getReference("rooms/${roomName}/players/${maxPlayerVoted?.id}/saveByWitch")
                                                            .addListenerForSingleValueEvent(object :
                                                                ValueEventListener {
                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    val saveByWith =
                                                                        snapshot.getValue<Boolean>()
                                                                            ?: false
                                                                    if (saveByWith) {
                                                                        firebaseDatabase.getReference(
                                                                            "rooms/${roomName}/healPower"
                                                                        )
                                                                            .setValue(false)
                                                                        resetPlayersStatus(
                                                                            roomName,
                                                                            uid
                                                                        )
                                                                        startNewDay(roomName, uid)
                                                                        updateRoomDayAndNight(
                                                                            roomName,
                                                                            uid,
                                                                            false
                                                                        )
                                                                    } else {
                                                                        if (maxPlayerVoted?.protected == true) {
                                                                            resetPlayersStatus(
                                                                                roomName,
                                                                                uid
                                                                            )
                                                                            startNewDay(
                                                                                roomName,
                                                                                uid
                                                                            )
                                                                            updateRoomDayAndNight(
                                                                                roomName,
                                                                                uid,
                                                                                false
                                                                            )
                                                                        } else {
//                                                        firebaseDatabase.getReference("rooms/${roomName}/players/${maxPlayerVoted?.id}/dead").setValue(true)
                                                                            resetPlayersStatus(
                                                                                roomName,
                                                                                uid
                                                                            )
                                                                            startNewDay(
                                                                                roomName,
                                                                                uid
                                                                            )
                                                                            updateRoomDayAndNight(
                                                                                roomName,
                                                                                uid,
                                                                                false
                                                                            )
                                                                        }
                                                                    }
                                                                }

                                                                override fun onCancelled(error: DatabaseError) {

                                                                }
                                                            })


                                                    }

                                                }.start()

                                            } else { // there no one player being target

                                                timer = object : CountDownTimer(15000L, 1000L) {
                                                    override fun onTick(millisUntilFinished: Long) {
                                                        _timeCountdown.postValue(Resource.Success((millisUntilFinished / 1000).toString()))
                                                        if (isWitch == WITCH) {
                                                            _playerBeingTargeted.postValue(
                                                                Resource.Success(
                                                                    PlayerInGame()
                                                                )
                                                            )

                                                        }
                                                    }

                                                    override fun onFinish() {
                                                        if (isWitch == WITCH) {
                                                            _playerBeingTargeted.postValue(
                                                                Resource.Error(
                                                                    Exception("")
                                                                )
                                                            )
                                                        }
                                                        resetPlayersStatus(roomName, uid)
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
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


    }


    fun startNewNight(roomName: String, uid: String) {

        timer = object : CountDownTimer(15000L, 1000L) {
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
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val playerCreateRoomId = snapshot.child("playerCreateRoomId").getValue<String>()
                    if (uid == playerCreateRoomId) {
                        if (day) {
                            val currentDay = snapshot.child("days").getValue<Int>()
                            if (currentDay != null) {
                                reference.updateChildren(
                                    mapOf(
                                        "days" to currentDay + 1, "day" to false
                                    )
                                )
                            }
                        } else {
                            val currentNight = snapshot.child("nights").getValue<Int>()
                            if (currentNight != null) {
                                reference.updateChildren(
                                    mapOf(
                                        "nights" to currentNight + 1,
                                        "day" to true,
                                        "witchPhase" to false
                                    )
                                )
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

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
                    error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
                ) {

                }

            })
    }


    fun playerCreateRoom(roomName: String, oldPlayerId: String) {
        val reference = firebaseDatabase.getReference("rooms/${roomName}/players/${oldPlayerId}")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    firebaseDatabase.reference.child("rooms/${roomName}").get()
                        .addOnSuccessListener {
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

                        val room = snapshot.getValue(Room::class.java)
//                        val name = snapshot.child("name").getValue(String::class.java)

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
        roomName: String, message: String, playerName: String
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


    fun getRoomHarmPower(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/harmPower")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue<Boolean>() ?: false
                        _harmPowerStatus.postValue(Resource.Success(status))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _harmPowerStatus.postValue(Resource.Error(error.toException()))
                }

            })
    }


    fun getRoomGameEnded(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/gameEnded")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue<Boolean>() ?: false
                        _gameEndedStatus.postValue(Resource.Success(status))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _gameEndedStatus.postValue(Resource.Error(error.toException()))
                }

            })
    }

    fun getRoomWitchPhase(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/witchPhase")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue<Boolean>() ?: false
                        _witchPhaseStatus.postValue(Resource.Success(status))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _witchPhaseStatus.postValue(Resource.Error(error.toException()))
                }

            })
    }

    fun getRoomGameStarted(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/gameStarted")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue<Boolean>() ?: false
                        _gameStartedStatus.postValue(Resource.Success(status))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _gameStartedStatus.postValue(Resource.Error(error.toException()))
                }

            })
    }


    fun getRoomSeerPicked(roomName: String) {
        firebaseDatabase.getReference("rooms/${roomName}/seerPicked")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue<Boolean>() ?: false
                        _roomSeerPickedStatus.postValue(Resource.Success(status))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _roomSeerPickedStatus.postValue(Resource.Error(error.toException()))

                }
            })
    }

    fun getRoomIsDayStatus(roomName: String) {
        firebaseDatabase.getReference("/rooms/${roomName}/day")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue<Boolean>() ?: false
                        _roomIsDayStatus.postValue(Event(Resource.Success(status)))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _roomIsDayStatus.postValue(Event(Resource.Error(error.toException())))
                }

            })
    }






}