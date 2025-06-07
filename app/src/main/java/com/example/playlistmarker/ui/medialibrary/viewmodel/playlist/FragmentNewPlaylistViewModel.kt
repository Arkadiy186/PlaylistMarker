package com.example.playlistmarker.ui.medialibrary.viewmodel.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.use_cases.PlaylistDbInteractor
import kotlinx.coroutines.launch

class FragmentNewPlaylistViewModel(private val playlistDbInteractor: PlaylistDbInteractor) : ViewModel() {
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModelScope.launch {
            playlistDbInteractor.getPlaylists().collect { list ->
                _playlists.postValue(list)
            }
        }
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistDbInteractor.insertPlaylist(playlist)
        }
    }

    fun removePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistDbInteractor.deletePlaylist(playlist)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistDbInteractor.updatePlaylist(playlist)
        }
    }
}