package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.api.BaseAuth
import com.example.superwolffirebase.models.Player
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    repository: BaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {


    private val _playerInfo = MutableLiveData<Event<Resource<Player>>>()
    val playerInfo get() = _playerInfo

    init {
        try {
            _playerInfo.postValue(Event(Resource.Loading))
            if (repository.currentUser != null) {
                val uid = repository.currentUser!!.uid
                firebaseDatabase.getReference("players").child(uid).get()
                    .addOnSuccessListener {
                        val player = Player(
                            uid,
                            it.child("name").value as String,
                            it.child("avatar").value as String,
                            it.child("gender").value as String,
                            it.child("email").value as String,
                            it.child("role").value as String,
                            it.child("status").value as String,
                        )
                        _playerInfo.postValue(Event(Resource.Success(player)))
                    }

            }
        } catch (e: ExceptionInInitializerError) {
            _playerInfo.postValue(Event(Resource.Error(Exception())))

        }
    }


}