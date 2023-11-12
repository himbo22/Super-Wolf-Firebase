package com.example.superwolffirebase.viewmodel.activityviewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.other.Event
import com.example.superwolffirebase.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class TutorialViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    private val _checkSkipStatus = MutableLiveData<Event<Resource<Int>>>()
    val checkSkipStatus get() = _checkSkipStatus
    private val _checkCompletedStatus =MutableLiveData<Resource<Int>>()
    val checkCompletedStatus get() = _checkCompletedStatus
    fun putBoolean() {
        sharedPreferences.edit().putBoolean("skip-tutorial", true).apply()
    }


    fun check() {
        _checkSkipStatus.postValue(Event(Resource.Loading))
        if (sharedPreferences.getBoolean("skip-tutorial", false)) {
            _checkSkipStatus.postValue(Event(Resource.Success(1)))
        } else {
            _checkSkipStatus.postValue(Event((Resource.Error(Exception("Nothing")))))
        }
    }

}