package com.example.superwolffirebase.viewmodel.activityviewmodel

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.SetUpProfile
import com.example.superwolffirebase.other.Resource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetUpProfileViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
    private val sharedPreferences: SharedPreferences,
    private val repository: SetUpProfile
) : ViewModel() {

    private val _uploadProfileStatus = MutableLiveData<Resource<DatabaseReference>>()
    val uploadProfileStatus get() = _uploadProfileStatus


    fun isCompletedProfile() {
        sharedPreferences.edit().putString("completedProfile", "yes").apply()
    }

    fun unFinishedProfile() {
        sharedPreferences.edit().putString("completedProfile", "unfinished").apply()
    }

    fun saveProfile(
        uri: Uri,
        id: String,
        name: String,
        gender: String,
        email: String,

    ) = viewModelScope.launch(Dispatchers.IO) {
        _uploadProfileStatus.postValue(Resource.Loading)
        val result = async {
            repository.uploadProfile(uri, id, name, gender, email)
        }.await()
        _uploadProfileStatus.postValue(result)
    }

//    fun saveAvatar(
//        uri: Uri,
//        id: String,
//        name: String,
//        gender: String,
//        email: String,
//        role: String,
//        status: String
//    ) {
//        _uploadProfileStatus.postValue(Resource.Loading)
//        val reference = firebaseStorage.reference.child("Avatars").child(Date().time.toString())
//        reference.putFile(uri).addOnCompleteListener {
//            if (it.isSuccessful) {
//                reference.downloadUrl.addOnSuccessListener { task ->
//                    val player = Player(id, name, task.toString(), gender, email, role, status)
//                    firebaseDatabase.reference.child("players")
//                        .child(id)
//                        .setValue(player)
//                        .addOnSuccessListener {
//                            _uploadProfileStatus.postValue(Resource.Success(firebaseDatabase))
//                        }
//                        .addOnFailureListener { exp ->
//                            _uploadProfileStatus.postValue(Resource.Error(exp))
//                        }
//                }
//            }
//        }
//    }

}