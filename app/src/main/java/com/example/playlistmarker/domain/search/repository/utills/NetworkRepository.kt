package com.example.playlistmarker.domain.search.repository.utills

interface NetworkRepository {
    fun isInternetAvailable(): Boolean
}