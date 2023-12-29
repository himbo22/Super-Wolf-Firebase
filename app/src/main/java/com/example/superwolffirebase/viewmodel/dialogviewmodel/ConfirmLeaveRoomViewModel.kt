package com.example.superwolffirebase.viewmodel.dialogviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.JoinLeaveRoom
import com.example.superwolffirebase.api.SoundTrack
import com.example.superwolffirebase.other.Constant.DAY
import com.example.superwolffirebase.other.Constant.NIGHT
import com.example.superwolffirebase.other.Constant.WAITING
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmLeaveRoomViewModel @Inject constructor(
    private val leaveRoom: JoinLeaveRoom,
    private val firebaseDatabase: FirebaseDatabase,
    private val soundTrack: SoundTrack
) : ViewModel() {

    fun stopMusic(){
        soundTrack.stop(WAITING)
        soundTrack.stop(DAY)
        soundTrack.stop(NIGHT)
    }

    fun leaveRoom(roomName: String, uid: String) = viewModelScope.launch {
        leaveRoom.leaveRoom(roomName, uid)
//        firebaseDatabase.getReference("rooms/${roomName}").addListenerForSingleValueEvent(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val gameStarted = snapshot.child("gameStarted").getValue<Boolean>()
//                if (gameStarted == true){
//                    firebaseDatabase.getReference("rooms/${roomName}/players/${uid}/dead").setValue(true)
//                } else {
//                    val amount = snapshot.child("amount").getValue<Int>()
//                    if (amount == 0){
//                        firebaseDatabase.getReference("rooms/${roomName}").removeValue()
//                    } else {
//                        firebaseDatabase.getReference("rooms/${roomName}/players/${uid}").removeValue()
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })

    }
}