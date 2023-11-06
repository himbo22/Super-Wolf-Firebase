package com.example.superwolffirebase.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _completedProfileStatus = MutableLiveData<Event<Resource<String>>>()
    val completedProfileStatus get() = _completedProfileStatus

    fun checkProfileStatus(){
        if(sharedPreferences.getString("completedProfile", null) == "unfinished"){
            _completedProfileStatus.postValue(Event(Resource.Success("")))
        } else {
            _completedProfileStatus.postValue(Event(Resource.Error(Exception())))
        }
    }

}