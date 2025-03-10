package com.example.playlistmarker.ui.search.viewmodel.searchviewmodel

import com.example.playlistmarker.ui.search.model.TrackInfoDetails

interface SearchView {
    fun showTracks(track: List<TrackInfoDetails>)
    fun showLoading(isLoading: Boolean)
    fun showNotFound()
    fun showErrorInternet()
    fun showHistory(trackHistory: List<TrackInfoDetails>)
}