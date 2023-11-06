package com.example.superwolffirebase.viewmodel

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.models.Player
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SetUpProfileViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uploadProfileStatus = MutableLiveData<Resource<FirebaseDatabase>>()
    val uploadProfileStatus get() = _uploadProfileStatus


    fun isCompletedProfile() {
        sharedPreferences.edit().putString("completedProfile", "yes").apply()
    }

    fun unFinishedProfile() {
        sharedPreferences.edit().putString("completedProfile", "unfinished").apply()
    }


    fun saveAvatar(
        uri: Uri,
        id: String,
        name: String,
        gender: String,
        email: String,
        role: String,
        status: String
    ) {
        val reference = firebaseStorage.reference.child("Avatars").child(Date().time.toString())
        reference.putFile(uri).addOnCompleteListener {
            if (it.isSuccessful) {
                reference.downloadUrl.addOnSuccessListener { task ->
                    val player = Player(id, name, task.toString(), gender, email, role, status)
                    firebaseDatabase.reference.child("players")
                        .child(id)
                        .setValue(player)
                        .addOnSuccessListener {
                            _uploadProfileStatus.postValue(Resource.Success(firebaseDatabase))
                        }
                        .addOnFailureListener { exp ->
                            _uploadProfileStatus.postValue(Resource.Error(exp))
                        }
                }
            }
        }
    }

}