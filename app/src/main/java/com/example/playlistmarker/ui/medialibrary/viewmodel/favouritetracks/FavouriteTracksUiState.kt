package com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks

import com.example.playlistmarker.ui.search.model.TrackInfoDetails

sealed class FavouriteTracksUiState {
    data class Content(val tracks: List<TrackInfoDetails>) : FavouriteTracksUiState()
    data class Placeholder(val message: Int) : FavouriteTracksUiState()
}