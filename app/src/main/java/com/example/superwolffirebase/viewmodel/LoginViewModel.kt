package com.example.superwolffirebase.viewmodel

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
class LoginViewModel @Inject constructor(
    private val repository: BaseAuth
) : ViewModel() {

    private val _loginResponse = MutableLiveData<Event<Resource<FirebaseUser>>>()
    val loginResponse get() = _loginResponse


    init {
        if (repository.currentUser != null) {
            _loginResponse.postValue(Event(Resource.Success(repository.currentUser!!)))
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginResponse.postValue(Event(Resource.Loading))
        val result = repository.login(email, password)
        _loginResponse.postValue(result)
    }

}