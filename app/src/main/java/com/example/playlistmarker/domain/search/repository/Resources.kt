package com.example.playlistmarker.domain.search.repository

sealed class Resources<T> {
    class Success<T>(val data: T): Resources<T>()
    class Error<T>(val message: String, val data: T? = null): Resources<T>()
}