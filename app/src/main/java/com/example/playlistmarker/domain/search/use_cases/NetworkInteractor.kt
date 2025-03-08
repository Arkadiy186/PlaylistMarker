package com.example.playlistmarker.domain.search.use_cases

interface NetworkInteractor {
    fun isInternetAvailable(): Boolean
}