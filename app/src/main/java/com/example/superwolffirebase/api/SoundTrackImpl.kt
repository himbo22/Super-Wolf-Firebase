package com.example.superwolffirebase.api

import android.media.MediaPlayer
import com.example.superwolffirebase.other.Constant.DAY
import com.example.superwolffirebase.other.Constant.MAIN_MENU
import com.example.superwolffirebase.other.Constant.NIGHT
import com.example.superwolffirebase.other.Constant.WAITING
import javax.inject.Inject
import javax.inject.Named

class SoundTrackImpl @Inject constructor(
    @Named("main_menu") val mainMenuMusic: MediaPlayer,
    @Named("day") val dayMusic: MediaPlayer,
    @Named("night_witch_phase") val nightAndWitchPhase: MediaPlayer,
    @Named("waiting") val waitingMusic: MediaPlayer
) : SoundTrack {


    override fun play(music: String) {
        when (music) {
            MAIN_MENU -> {
                if (!mainMenuMusic.isPlaying) mainMenuMusic.start()
            }

            WAITING -> {
                if (!waitingMusic.isPlaying) waitingMusic.start()
            }

            DAY -> {
                if (!dayMusic.isPlaying) dayMusic.start()
            }

            NIGHT -> {
                if (!nightAndWitchPhase.isPlaying) nightAndWitchPhase.start()
            }
        }

    }


    override fun release(music: String) {
        when (music) {
            MAIN_MENU -> {
                mainMenuMusic.stop()
                mainMenuMusic.release()
            }

            WAITING -> {
                waitingMusic.stop()
                waitingMusic.release()
            }

            DAY -> {
                dayMusic.stop()
                dayMusic.release()
            }

            NIGHT -> {
                nightAndWitchPhase.stop()
                nightAndWitchPhase.release()
            }
        }
    }


    override fun stop(music: String) {
        when (music) {
            MAIN_MENU -> {
                mainMenuMusic.pause()
                mainMenuMusic.seekTo(0)
            }

            WAITING -> {
                if (waitingMusic.isPlaying){
                    waitingMusic.pause()
                    waitingMusic.seekTo(0)
                }
            }

            DAY -> {
                if (dayMusic.isPlaying){
                    dayMusic.pause()
                    dayMusic.seekTo(0)
                }
            }

            NIGHT -> {
                if (dayMusic.isPlaying){
                    nightAndWitchPhase.pause()
                    nightAndWitchPhase.seekTo(0)
                }
            }
        }


    }


    override fun resume(music: String) {
        when (music) {
            MAIN_MENU -> {
                mainMenuMusic.seekTo(mainMenuMusic.currentPosition)
                mainMenuMusic.start()
            }

            WAITING -> {
                waitingMusic.seekTo(mainMenuMusic.currentPosition)
                waitingMusic.start()
            }

            DAY -> {
                dayMusic.seekTo(mainMenuMusic.currentPosition)
                dayMusic.start()
            }

            NIGHT -> {
                nightAndWitchPhase.seekTo(mainMenuMusic.currentPosition)
                nightAndWitchPhase.start()
            }
        }
    }

    override fun pause(music: String) {
        when (music) {
            MAIN_MENU -> {
                mainMenuMusic.pause()
            }

            WAITING -> {
                waitingMusic.pause()
            }

            DAY -> {
                dayMusic.pause()
            }

            NIGHT -> {
                nightAndWitchPhase.pause()
            }
        }
    }
}