package com.example.superwolffirebase.other

sealed class Resource<out R> {

    data class Success<out R>(val result: R) : Resource<R>()
    data class Error(val exception: Exception) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()

}
