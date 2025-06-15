package com.example.playlistmarker.ui.medialibrary.viewmodel.currentplaylist

import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.model.Track

sealed class UiStateCurrentPlaylistTracks {
    data class Content(val playlist: Playlist, val tracks: List<Track>) : UiStateCurrentPlaylistTracks()
}