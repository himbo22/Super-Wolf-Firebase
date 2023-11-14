package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.JoinLeaveRoom
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
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
class PlayViewModel @Inject constructor(
    private val leaveRoom: JoinLeaveRoom,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    private val _leaveRoomResult = MutableLiveData<Event<Resource<DatabaseReference>>>()
    val leaveRoomResult get() = _leaveRoomResult
    private val _allPlayer = MutableLiveData<Event<Resource<List<PlayerInGame>>>>()
    val allPlayer get() = _allPlayer


    init {
        firebaseDatabase.getReference("rooms").child("players").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val playerList = arrayListOf<PlayerInGame>()
                if (snapshot.exists()){
                    for (playerSnap in snapshot.children){
                        val player = playerSnap.getValue(PlayerInGame::class.java)
                        if (player != null) {
                            playerList.add(player)
                        }
                    }
                    _allPlayer.postValue(Event(Resource.Success(playerList)))
                } else {
                    _allPlayer.postValue(Event(Resource.Error(Exception("No data"))))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun leaveRoom(name: String, amount: Int, id: String)= viewModelScope.launch {
        _leaveRoomResult.postValue(Event(Resource.Loading))
        val result = leaveRoom.leaveRoom(name, amount, id)
        _leaveRoomResult.postValue(result)
    }


}