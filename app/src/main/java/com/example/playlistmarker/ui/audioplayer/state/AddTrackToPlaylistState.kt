package com.example.playlistmarker.ui.audioplayer.state

sealed class AddTrackToPlaylistState {
    data class TrackIsExists(val playlist: String) : AddTrackToPlaylistState()
    data class TrackAdded(val playlist: String) : AddTrackToPlaylistState()
}