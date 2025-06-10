package com.example.playlistmarker.ui.medialibrary.viewmodel.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.use_cases.PlaylistDbInteractor
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistDbInteractor: PlaylistDbInteractor,
    private val themeInteractor: ThemeInteractor) : ViewModel() {
    private val _playlistsState = MutableLiveData<PlaylistUiState>()
    val playlistsState: LiveData<PlaylistUiState> = _playlistsState

    private val _uiThemeLiveData = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> = _uiThemeLiveData

    init {
        observeViewModel()
        _uiThemeLiveData.postValue(themeInteractor.getTheme())
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