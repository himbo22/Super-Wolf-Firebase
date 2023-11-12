package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.BaseAuth
import com.example.superwolffirebase.api.SetUpProfile
import com.example.superwolffirebase.models.Player
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: BaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val setUpRepository: SetUpProfile
) : ViewModel() {


    private val _playerInfo = MutableLiveData<Event<Resource<Player>>>()
    val playerInfo get() = _playerInfo
    private val _updateProfile = MutableLiveData<Resource<DatabaseReference>>()
    val updateProfile get() = _updateProfile

    fun updateProfile(
        uri: Uri,
        id: String,
        name: String,
        gender: String,
        email: String,
        ) = viewModelScope.launch(Dispatchers.IO) {
        _updateProfile.postValue(Resource.Loading)
        val result = async {
            setUpRepository.uploadProfile(uri, id, name, gender, email)
        }.await()
        _updateProfile.postValue(result)
    }

    init {
        _playerInfo.postValue(Event(Resource.Loading))
        if (repository.currentUser != null) {
            val currentPlayer = repository.currentUser
            if (currentPlayer != null) {
                firebaseDatabase.getReference("players").child(currentPlayer.uid).get()
                    .addOnSuccessListener {
                        try {
                            val player = Player(
                                currentPlayer.uid,
                                it.child("name").value as String,
                                it.child("avatar").value as String,
                                it.child("gender").value as String,
                                it.child("email").value as String,
                            )
                            _playerInfo.postValue(Event(Resource.Success(player)))
                        } catch (e: Exception) {
                            _playerInfo.postValue(
                                Event(
                                    Resource.Success(
                                        Player(
                                            currentPlayer.uid,
                                            currentPlayer.displayName,
                                            null,
                                            "GENDER",
                                            currentPlayer.email
                                        )
                                    )
                                )
                            )
                        }
                    }
                    .addOnFailureListener {
                        _playerInfo.postValue(Event(Resource.Error(it)))
                    }
            }

        }
    }

    fun logOut() {
        repository.logOut()
    }


}