package com.example.superwolffirebase.viewmodel.dialogviewmodel

import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.api.BaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConfirmLogoutViewModel @Inject constructor(
    private val repository: BaseAuth
) : ViewModel(){

    fun logOut(){
        repository.logOut()
    }

}