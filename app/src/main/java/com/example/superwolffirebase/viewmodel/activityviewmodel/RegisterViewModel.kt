package com.example.superwolffirebase.viewmodel.activityviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superwolffirebase.api.BaseAuth
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: BaseAuth
) : ViewModel() {
    private val _registerResponse = MutableLiveData<Event<Resource<FirebaseUser>>>()
    val registerResponse get() = _registerResponse

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _registerResponse.postValue(Event(Resource.Loading))
        val result = repository.signUp(name, email, password)
        _registerResponse.postValue(result)
    }


}