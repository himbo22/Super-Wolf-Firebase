package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.api.BaseAuth
import com.example.superwolffirebase.models.Player
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val repository: BaseAuth
) : ViewModel() {

    private val _completeProfile = MutableLiveData<Resource<Player>>()
    val completeProfile get() = _completeProfile

    init {
        val currentPlayer = repository.currentUser
        _completeProfile.postValue(Resource.Loading)
        if (currentPlayer != null) {
            firebaseDatabase.getReference("players").child(currentPlayer.uid).get()
                .addOnSuccessListener {
                    if (it.exists()){
                        _completeProfile.postValue((Resource.Success(Player())))
                    } else {
                        _completeProfile.postValue((Resource.Error(Exception("You must complete your profile"))))
                    }
                }
        }


    }


}