package com.example.superwolffirebase.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.models.Player
import com.example.superwolffirebase.models.Roles
import com.example.superwolffirebase.models.Status
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetUpProfileViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uploadProfileStatus = MutableLiveData<Resource<FirebaseDatabase>>()
    val uploadProfileStatus get() = _uploadProfileStatus

    fun isCompletedProfile(){
        sharedPreferences.edit().putString("completedProfile", "yes").apply()
    }

    fun unFinishedProfile(){
        sharedPreferences.edit().putString("completedProfile", "unfinished").apply()
    }

    fun uploadProfile(id: String,
                      name: String,
                      avatar: String,
                      gender: String,
                      email: String,
                      password: String,
                      role: Roles,
                      status: Status
    ) {
        val player = Player(id, name, avatar, gender, email, password, role, status)
        firebaseDatabase.reference.child("players")
            .child(id)
            .setValue(player)
            .addOnSuccessListener {
                _uploadProfileStatus.postValue(Resource.Success(firebaseDatabase))
            }
            .addOnFailureListener {
                _uploadProfileStatus.postValue(Resource.Error(it))
            }
    }
}