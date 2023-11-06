package com.example.superwolffirebase.utils

import android.util.Log
import android.view.View

fun showLog(msg: String) {
    Log.d("hoangdeptrai", msg)
}

fun View.hide(){
    visibility = View.GONE
}

fun View.invisible(){
    visibility = View.INVISIBLE
}

fun View.show(){
    visibility = View.VISIBLE
}