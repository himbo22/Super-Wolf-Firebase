package com.example.superwolffirebase.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TutorialViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _checkSkipStatus = MutableLiveData<Resource<Int>>()
    val checkSkipStatus get() = _checkSkipStatus
    fun putBoolean() {
        sharedPreferences.edit().putBoolean("skip-tutorial", true).apply()
    }

    fun check() {
        if (sharedPreferences.getBoolean("skip-tutorial", false)) {
            _checkSkipStatus.postValue(Resource.Success(1))
        }
    }
}