package com.example.playlistmarker.ui.medialibrary.viewmodel.playlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.use_cases.PlaylistDbInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistDbInteractor: PlaylistDbInteractor) : ViewModel() {
    private val _playlistsState = MutableLiveData<PlaylistUiState>()
    val playlistsState: LiveData<PlaylistUiState> = _playlistsState

    init {
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModelScope.launch {
            playlistDbInteractor.getPlaylists().collect { list ->
                if (list.isEmpty()) {
                    _playlistsState.postValue(PlaylistUiState.Placeholder)
                } else {
                    _playlistsState.postValue(PlaylistUiState.Content(list))
                }
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