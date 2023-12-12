package com.example.superwolffirebase.viewmodel.dialogviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.JoinLeaveRoom
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmLeaveRoomViewModel @Inject constructor(
    private val leaveRoom: JoinLeaveRoom
) : ViewModel() {

    private val _leaveRoomStatus = MutableLiveData<Event<Resource<DatabaseReference>>>()
    val leaveRoomStatus = _leaveRoomStatus
    fun leaveRoom(roomName: String, uid: String) = viewModelScope.launch {
        val result = leaveRoom.leaveRoom(roomName, uid)
        _leaveRoomStatus.postValue(result)
    }
}