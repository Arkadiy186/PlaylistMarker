package com.example.playlistmarker.ui.medialibrary.viewmodel.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.use_cases.PlaylistDbInteractor
import com.example.playlistmarker.domain.db.use_cases.TrackDbInteractor
import com.example.playlistmarker.domain.db.use_cases.TrackPlaylistDbInteractor
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.medialibrary.viewmodel.currentplaylist.UiStateCurrentPlaylistTracks
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistDbInteractor: PlaylistDbInteractor,
    private val trackPlaylistDbInteractor: TrackPlaylistDbInteractor,
    private val themeInteractor: ThemeInteractor
) : ViewModel() {

    private val _playlistsState = MutableLiveData<PlaylistUiState>()
    val playlistsState: LiveData<PlaylistUiState> = _playlistsState

    private val _playlistDetailsState = MutableLiveData<UiStateCurrentPlaylistTracks>()
    val playlistDetailsState: LiveData<UiStateCurrentPlaylistTracks> = _playlistDetailsState

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

    fun deleteTrackFromPlaylist(trackId: Long, playlist: Playlist) {
        viewModelScope.launch {
            trackPlaylistDbInteractor.deleteTrack(trackId, playlist.id)
            loadPlaylistWithTracks(playlist.id)
        }
    }

    fun loadPlaylistWithTracks(playlistId: Long) {
        viewModelScope.launch {
            val playlistFlow = playlistDbInteractor.getPlaylistId(playlistId).first()

            val tracksFlow = trackPlaylistDbInteractor.getTracksForPlaylist(playlistId).map { list ->
                list.map { track ->
                    TrackInfoDetailsMapper.map(track)
                }
            }.first()

            _playlistDetailsState.postValue(UiStateCurrentPlaylistTracks.Content(playlistFlow, tracksFlow))
        }
    }


}