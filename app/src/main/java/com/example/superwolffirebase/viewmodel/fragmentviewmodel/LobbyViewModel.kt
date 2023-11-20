package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.BaseAuth
import com.example.superwolffirebase.api.CreateNewRoom
import com.example.superwolffirebase.api.JoinLeaveRoom
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val repository: CreateNewRoom,
    private val firebaseDatabase: FirebaseDatabase,
    private val baseAuth: BaseAuth,
    private val joinRoom: JoinLeaveRoom
) : ViewModel() {

    private val _newRoom = MutableLiveData<Resource<DatabaseReference>>()
    val newRoom get() = _newRoom
    private var _allRoom = MutableLiveData<Resource<List<Room>>>()
    val allRoom get() = _allRoom
    private val _joinRoomResult = MutableLiveData<Event<Resource<PlayerInGame>>>()
    val joinRoomResult get() = _joinRoomResult


    fun joinRoom(roomName: String,
                 amount: Int,
                 id: String,
                 avatar: String,
                 playerName : String,
                 role: String) = viewModelScope.launch {
        val result = joinRoom.joinRoom(roomName, amount,id, avatar, playerName, role)
        _joinRoomResult.postValue(result)
    }


    init {

        firebaseDatabase.getReference("rooms").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomListTemp = arrayListOf<Room>()
                if (snapshot.exists()){
                    for (roomSnap in snapshot.children){
                        
                        val room = roomSnap.getValue(Room::class.java)
                        if (room != null){
                            roomListTemp.add(room)
                        }
                    }
                    _allRoom.postValue(Resource.Success(roomListTemp))

                } else {
                    _allRoom.postValue(Resource.Error(Exception("Have no any rooms")))

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun createNewRoom(name: String) = viewModelScope.launch{
        _newRoom.postValue(Resource.Loading)
        val response = repository.createNewRoom(name)
        _newRoom.postValue(response)
    }



}