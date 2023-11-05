package com.example.superwolffirebase.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

fun showLog(msg: String) {
    Log.d("hoangdeptrai", msg)
}

fun makeToast(context: Context, msg: String){
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}