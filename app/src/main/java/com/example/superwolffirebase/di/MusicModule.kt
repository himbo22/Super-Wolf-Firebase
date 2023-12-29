package com.example.superwolffirebase.di

import android.content.Context
import android.media.MediaPlayer
import com.example.superwolffirebase.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicModule {
    @Provides
    @Singleton
    @Named("main_menu")
    fun provideMainMenuMusic(@ApplicationContext context: Context): MediaPlayer {
        val mediaPlayer = MediaPlayer.create(context, R.raw.main_menu)
        mediaPlayer.isLooping = true
        return mediaPlayer
    }

    @Provides
    @Singleton
    @Named("day")
    fun provideDayMusic(@ApplicationContext context: Context): MediaPlayer =
        MediaPlayer.create(context, R.raw.day1)


    @Provides
    @Singleton
    @Named("night_witch_phase")
    fun provideNightWitchPhase(@ApplicationContext context: Context): MediaPlayer =
        MediaPlayer.create(context, R.raw.night_witch_phase)

    @Provides
    @Singleton
    @Named("waiting")
    fun provideWaitingMusic(@ApplicationContext context: Context): MediaPlayer {
        val media = MediaPlayer.create(context, R.raw.waiting)
        media.isLooping = true
        return media
    }

}