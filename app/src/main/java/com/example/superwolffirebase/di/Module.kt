package com.example.superwolffirebase.di

import android.content.SharedPreferences
import com.example.superwolffirebase.api.BaseAuth
import com.example.superwolffirebase.api.BaseAuthImpl
import com.example.superwolffirebase.manager.SharePrefManager
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(impl: BaseAuthImpl): BaseAuth = impl

    @Provides
    @Singleton
    fun provideSharedPreferences(
        sharePrefManager: SharePrefManager
    ): SharedPreferences = sharePrefManager.getSharePref()

}