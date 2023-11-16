package com.example.superwolffirebase.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

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

fun Fragment.hideKeyboard(){
    view?.let {
        activity?.hideKeyboard(it)
    }
}

fun Activity.hideKeyboard(){
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View){
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}