package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.BaseAuth
import com.example.superwolffirebase.api.CreateNewRoom
import com.example.superwolffirebase.api.JoinLeaveRoom
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception


@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val repository: CreateNewRoom,
    private val firebaseDatabase: FirebaseDatabase,
    private val baseAuth: BaseAuth,
    private val joinRoom: JoinLeaveRoom
) : ViewModel() {

    private val _newRoom = MutableLiveData<Event<Resource<Pair<Room,PlayerInGame>>>>()
    val newRoom = _newRoom
    private var _allRoom = MutableLiveData<Resource<List<Room>>>()
    val allRoom get() = _allRoom
    private val _joinRoomResult = MutableLiveData<Event<Resource<PlayerInGame>>>()
    val joinRoomResult  = _joinRoomResult


    fun joinRoom(
        roomName: String,
        id: String,
        avatar: String,
        playerName: String
    ) = viewModelScope.launch {
        val result = joinRoom.joinRoom(roomName, id, avatar, playerName)
        _joinRoomResult.postValue(result)
    }



    fun getAllRooms(){
        firebaseDatabase.getReference("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomListTemp = arrayListOf<Room>()
                try {
                    if (snapshot.exists()) {
                        for (roomSnap in snapshot.children) {

                            val room = roomSnap.getValue(Room::class.java)



                            if (room != null) {
                                roomListTemp.add(room)
                            }
                        }
                        _allRoom.postValue(Resource.Success(roomListTemp))

                    } else {
                        _allRoom.postValue(Resource.Error(Exception("Have no any rooms")))

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                _allRoom.postValue(Resource.Error(error.toException()))
            }

        })
    }


    fun createNewRoom(name: String, playerId: String, avatar: String, playerName: String) = viewModelScope.launch {
        _newRoom.postValue(Event(Resource.Loading))
        val response = repository.createNewRoom(name, playerId, avatar, playerName)
        _newRoom.postValue((response))
    }


}