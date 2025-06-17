package com.example.playlistmarker.ui.medialibrary.viewmodel.currentplaylist

import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

sealed class UiStateCurrentPlaylistTracks {
    data class Content(val playlist: Playlist, val tracks: List<TrackInfoDetails>) : UiStateCurrentPlaylistTracks()
}