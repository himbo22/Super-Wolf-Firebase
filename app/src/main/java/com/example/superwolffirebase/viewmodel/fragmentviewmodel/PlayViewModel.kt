package com.example.superwolffirebase.viewmodel.fragmentviewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PlayViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
) : ViewModel() {

}