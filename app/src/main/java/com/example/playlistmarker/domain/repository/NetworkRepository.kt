package com.example.playlistmarker.domain.repository

interface NetworkRepository {
    fun isInternetAvailable(): Boolean
}