package com.example.playlistmarker.ui.search.viewmodel.searchviewmodel

import com.example.playlistmarker.ui.search.model.TrackInfoDetails

sealed class UiState {
    object NotFound : UiState()
    data class Loading(val isLoading: Boolean) : UiState()
    data class ErrorInternet(val message: Int) : UiState()
    data class Content(val tracks: List<TrackInfoDetails>) : UiState()
}