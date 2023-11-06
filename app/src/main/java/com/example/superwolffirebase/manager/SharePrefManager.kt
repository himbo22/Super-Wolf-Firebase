package com.example.superwolffirebase.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharePrefManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getSharePref(): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            "app",
            getMasterKey(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getMasterKey(): MasterKey {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        } else {
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }
    }
}