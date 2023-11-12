package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.CreateNewRoom
import com.example.superwolffirebase.models.Room
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
class LobbyViewModel @Inject constructor(
    private val repository: CreateNewRoom,
    private val firebaseDatabase: FirebaseDatabase

) : ViewModel() {

    private val _newRoom = MutableLiveData<Resource<DatabaseReference>>()
    val newRoom get() = _newRoom
    private var _allRoom = MutableLiveData<Resource<List<Room>>>()
    val allRoom get() = _allRoom


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