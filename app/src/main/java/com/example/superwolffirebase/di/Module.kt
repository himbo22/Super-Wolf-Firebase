package com.example.superwolffirebase.di

import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import com.example.superwolffirebase.api.BaseAuth
import com.example.superwolffirebase.api.BaseAuthImpl
import com.example.superwolffirebase.api.CreateNewRoom
import com.example.superwolffirebase.api.CreateNewRoomImpl
import com.example.superwolffirebase.api.JoinLeaveRoom
import com.example.superwolffirebase.api.JoinLeaveRoomImpl
import com.example.superwolffirebase.api.SendMessage
import com.example.superwolffirebase.api.SendMessageImpl
import com.example.superwolffirebase.api.SetUpProfile
import com.example.superwolffirebase.api.SetUpProfileImpl
import com.example.superwolffirebase.manager.SharePrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
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
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()


    @Provides
    @Singleton
    fun provideAuthRepository(impl: BaseAuthImpl): BaseAuth = impl


    @Provides
    @Singleton
    fun provideSetUpProfileRepository(impl: SetUpProfileImpl): SetUpProfile = impl

    @Provides
    @Singleton
    fun provideCreateNewRoomRepository(impl: CreateNewRoomImpl): CreateNewRoom = impl

    @Provides
    @Singleton
    fun provideJoinLeaveRoomRepository(impl: JoinLeaveRoomImpl): JoinLeaveRoom = impl

    @Provides
    @Singleton
    fun provideSendMessageRepository(impl: SendMessageImpl): SendMessage = impl


    @Provides
    @Singleton
    fun provideSharedPreferences(
        sharePrefManager: SharePrefManager
    ): SharedPreferences = sharePrefManager.getSharePref()

}