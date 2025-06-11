package com.example.playlistmarker.ui.medialibrary.viewmodel.playlist

import com.example.playlistmarker.domain.db.model.Playlist

sealed class PlaylistUiState {
    data class Content(val playlists: List<Playlist>): PlaylistUiState()
    object Placeholder: PlaylistUiState()
}