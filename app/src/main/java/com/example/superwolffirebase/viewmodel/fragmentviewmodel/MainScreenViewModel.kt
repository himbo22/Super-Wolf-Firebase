package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.api.BaseAuth
import com.example.superwolffirebase.api.SoundTrack
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.other.Constant
import com.example.superwolffirebase.other.Constant.MAIN_MENU
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val repository: BaseAuth,
    private val soundTrack: SoundTrack
) : ViewModel() {

    private val _completeProfile = MutableLiveData<Resource<Player>>()
    val completeProfile get() = _completeProfile


    fun playMusic(){
        soundTrack.play(MAIN_MENU)
    }

    fun theBeginning(){
        _completeProfile.postValue(Resource.Loading)
        val currentPlayer = repository.currentUser
        if (currentPlayer != null) {
            firebaseDatabase.getReference("players").child(currentPlayer.uid).get()
                .addOnSuccessListener {
                    if (it.exists()){
                        val player = it.getValue<Player>()
                        _completeProfile.postValue((Resource.Success(player!!)))
                    } else {
                        _completeProfile.postValue((Resource.Error(Exception("You must complete your profile"))))
                    }
                }
        }


    }


}