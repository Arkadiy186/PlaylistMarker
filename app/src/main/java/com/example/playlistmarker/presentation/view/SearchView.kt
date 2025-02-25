package com.example.playlistmarker.presentation.view

import com.example.playlistmarker.presentation.model.TrackInfoDetails

interface SearchView {
    fun showTracks(track: List<TrackInfoDetails>)
    fun showLoading(isLoading: Boolean)
    fun showNotFound()
    fun showErrorInternet()
    fun showHistory(trackHistory: List<TrackInfoDetails>)
}