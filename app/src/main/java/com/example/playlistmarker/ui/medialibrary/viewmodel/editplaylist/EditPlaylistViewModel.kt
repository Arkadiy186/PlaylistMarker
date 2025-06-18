package com.example.playlistmarker.ui.medialibrary.viewmodel.editplaylist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.use_cases.PlaylistDbInteractor
import com.example.playlistmarker.domain.db.use_cases.TrackPlaylistDbInteractor
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.PlaylistViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistDbInteractor: PlaylistDbInteractor,
    private val trackPlaylistDbInteractor: TrackPlaylistDbInteractor,
    private val themeInteractor: ThemeInteractor
) : PlaylistViewModel(playlistDbInteractor, trackPlaylistDbInteractor, themeInteractor) {

    private val _currentPlaylist = MutableLiveData<Playlist?>()
    val currentPlaylist: MutableLiveData<Playlist?> = _currentPlaylist

    fun loadPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val currentPlaylist = playlistDbInteractor.getPlaylistId(playlist.id).first()
            _currentPlaylist.postValue(currentPlaylist)
        }
    }
}