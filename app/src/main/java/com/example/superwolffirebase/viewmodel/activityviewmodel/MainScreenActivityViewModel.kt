package com.example.superwolffirebase.viewmodel.activityviewmodel

import androidx.lifecycle.ViewModel
import com.example.superwolffirebase.api.SoundTrack
import com.example.superwolffirebase.other.Constant.DAY
import com.example.superwolffirebase.other.Constant.MAIN_MENU
import com.example.superwolffirebase.other.Constant.NIGHT
import com.example.superwolffirebase.other.Constant.WAITING
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenActivityViewModel @Inject constructor(
    private val soundTrack: SoundTrack
) : ViewModel()  {
    fun pauseMusic(){
        soundTrack.pause(MAIN_MENU)

    }
    fun resumeMusic(){
        soundTrack.resume(MAIN_MENU)
    }

    fun releaseMusic(){
        soundTrack.release(MAIN_MENU)
        soundTrack.release(WAITING)
        soundTrack.release(DAY)
        soundTrack.release(NIGHT)
    }
}