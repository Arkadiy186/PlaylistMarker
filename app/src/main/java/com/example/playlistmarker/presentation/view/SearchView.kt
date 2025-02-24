package com.example.playlistmarker.presentation.view

import com.example.playlistmarker.presentation.model.TrackInfo

interface SearchView {
    fun showTracks(track: List<TrackInfo>)
    fun showLoading(isLoading: Boolean)
    fun showNotFound()
    fun showErrorInternet()
    fun showHistory(trackHistory: List<TrackInfo>)
}