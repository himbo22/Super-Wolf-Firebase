package com.example.superwolffirebase.api

interface SoundTrack {
    fun play(music: String)
    fun stop(music: String)
    fun resume(music: String)
    fun pause(music: String)

    fun release(music: String)
}