package com.example.playlistmarker.ui.audioplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.use_cases.PlaylistDbInteractor
import com.example.playlistmarker.domain.db.use_cases.TrackDbInteractor
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.domain.player.use_cases.PositionTimeInteractor
import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import com.example.playlistmarker.ui.audioplayer.state.UiFavoriteButtonState
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.audioplayer.state.AddTrackToPlaylistState
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.PlaylistUiState
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AudioPlayerViewModel (
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val positionTimeInteractor: PositionTimeInteractor,
    private val trackDbInteractor: TrackDbInteractor,
    private val playlistDbInteractor: PlaylistDbInteractor,
    private val themeInteractor: ThemeInteractor
) : ViewModel(), AudioPlayerCallback {

    private val _playerState = MutableLiveData<UiAudioPlayerState>().apply { value = UiAudioPlayerState.Default() }
    val playerState: LiveData<UiAudioPlayerState> = _playerState

    private val _currentTime = MutableLiveData<String>().apply { value = "00:00" }
    val currentTime: LiveData<String> = _currentTime

    private val _currentTrack = MutableLiveData<TrackInfoDetails>()
    val currentTrack: LiveData<TrackInfoDetails> = _currentTrack

    private val _savedPosition = MutableLiveData<Int>().apply { value = 0 }
    val savedPosition: LiveData<Int> = _savedPosition

    private val _playerInfo = MediatorLiveData<PlayerInfo>()
    val playerInfo: LiveData<PlayerInfo> = _playerInfo

    private val _favouriteButtonState = MediatorLiveData<UiFavoriteButtonState>()
    val favouriteButtonState: LiveData<UiFavoriteButtonState> = _favouriteButtonState

    private val _showPlaylistEvent = MutableLiveData<TrackInfoDetails>()
    val showPlaylistEvent: LiveData<TrackInfoDetails> = _showPlaylistEvent

    private val _playlistUiState = MutableLiveData<PlaylistUiState>()
    val playlistUiState: LiveData<PlaylistUiState> = _playlistUiState

    private val _addTrackState = MutableLiveData<AddTrackToPlaylistState>()
    val addTrackState: LiveData<AddTrackToPlaylistState> = _addTrackState

    private val _uiThemeLiveData = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> = _uiThemeLiveData

    val track = TrackInfoDetails(
        1,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        false)

    private var timerJob: Job?= null
    private var currentPosition: Int = 0

    init {
        audioPlayerInteractor.setCallback(this)

        val update = {
            _playerInfo.value = PlayerInfo(
                audioPlayerInteractor.getPlayerState(),
                _currentTime.value ?: "00:00",
                _currentTrack.value ?: track,
                _savedPosition.value ?: 0
            )
        }

        _playerInfo.addSource(playerState) {update()}
        _playerInfo.addSource(currentTime) {update()}
        _playerInfo.addSource(currentTrack) {update()}
        _playerInfo.addSource(savedPosition) {update()}

        _favouriteButtonState.addSource(currentTrack) { track ->
            _favouriteButtonState.value = if (track.isFavourite) {
                UiFavoriteButtonState.IsFavourite()
            } else {
                UiFavoriteButtonState.NotFavourite()
            }
        }

        _uiThemeLiveData.postValue(themeInteractor.getTheme())
    }

    override fun onPlayerStateChanged(state: UiAudioPlayerState) {
        _playerState.postValue(state)

        when(state) {
            is UiAudioPlayerState.Playing -> startTimer()
            is UiAudioPlayerState.Completed -> {
                stopTimer()
                _currentTime.postValue("00:00")
                _currentTrack.value?.let {
                    audioPlayerInteractor.preparePlayer(it)
                    audioPlayerInteractor.startPlayer()
                }
            }
            else -> stopTimer()
        }
    }

    fun prepareTrack(track: TrackInfoDetails) {
        viewModelScope.launch {
            val favouriteIds = trackDbInteractor.getAllFavouriteTracks().first()
            val isFavourite = track.id in favouriteIds
            val updatedTrack = track.copy(isFavourite = isFavourite)
            _currentTrack.postValue(updatedTrack)

            audioPlayerInteractor.preparePlayer(updatedTrack)
        }
    }

    fun playTrack() {
        val playerState = audioPlayerInteractor.getPlayerState()

        if (playerState is UiAudioPlayerState.Playing) return

        when(playerState) {
            is UiAudioPlayerState.Prepared,
            is UiAudioPlayerState.Paused -> {
                audioPlayerInteractor.startPlayer()
            }
            is UiAudioPlayerState.Completed -> {
            }
            else -> {}
        }
    }

    fun pauseTrack() {
        savePosition()
        audioPlayerInteractor.pausePlayer()
        updateState { UiAudioPlayerState.Paused(it) }
        stopTimer()
    }

    fun stopTrack() {
        savePosition()
        audioPlayerInteractor.stopPlayer()
        updateStateAsPrepared()
        stopTimer()
    }

    fun seekTo(position: Int) {
        audioPlayerInteractor.seekTo(position)
        currentPosition = position
        _currentTime.postValue(formatTime(position))
    }

    fun onFavoriteClicked() {
        val current = _currentTrack.value ?: return

        viewModelScope.launch {
            val domainTrack = TrackInfoDetailsMapper.mapToDomain(current)
            if (current.isFavourite) {
                trackDbInteractor.deleteTrack(domainTrack)
            } else {
                trackDbInteractor.insertTrack(domainTrack)
            }

            _favouriteButtonState.postValue(
                if (!current.isFavourite) UiFavoriteButtonState.IsFavourite()
                else UiFavoriteButtonState.NotFavourite()
            )

            _currentTrack.value = current.copy(isFavourite = !current.isFavourite)
        }
    }

    fun addToPlaylistClicked() {
        val current = _currentTrack.value ?: return
        _showPlaylistEvent.postValue(current)
    }

    fun observeAddPlaylistViewModel() {
        viewModelScope.launch {
            playlistDbInteractor.getPlaylists().collect { list ->
                if (list.isEmpty()) {
                    _playlistUiState.postValue(PlaylistUiState.Placeholder)
                } else {
                    _playlistUiState.postValue(PlaylistUiState.Content(list))
                }
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist, track: TrackInfoDetails) {
        viewModelScope.launch {
            val idList = playlist.listIdTracks.split(",").mapNotNull { it.toIntOrNull() }.toMutableList()

            if (idList.contains(track.id)) {
                _addTrackState.postValue(AddTrackToPlaylistState.TrackIsExists(playlist.name))
            } else {
                idList.add(track.id)
                val updatedPlaylist = playlist.copy(
                    listIdTracks = idList.joinToString(","),
                    counterTracks = idList.size
                )

                playlistDbInteractor.updatePlaylist(updatedPlaylist)
                playlistDbInteractor.insertTrack(TrackInfoDetailsMapper.mapToDomain(track))

                _addTrackState.postValue(AddTrackToPlaylistState.TrackAdded((playlist.name)))
            }
        }
    }


    private fun updateState(state: (Int) -> UiAudioPlayerState) {
        currentPosition = audioPlayerInteractor.getCurrentPositionPlayer()
        _playerState.postValue(state(currentPosition))
    }

    private fun updateStateAsPrepared() {
        currentPosition = 0
        _playerState.postValue(UiAudioPlayerState.Prepared())
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(DELAY_TIMER)

                val state = audioPlayerInteractor.getPlayerState()
                if (state !is UiAudioPlayerState.Playing) break

                currentPosition = audioPlayerInteractor.getCurrentPositionPlayer()
                val formatted = formatTime(currentPosition)
                _currentTime.postValue(formatted)
                _playerState.postValue(UiAudioPlayerState.Playing(currentPosition))
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun savePosition() {
        positionTimeInteractor.saveCurrentPosition(currentPosition)
    }

    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = ms / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    companion object {
        const val DELAY_TIMER = 300L
    }
}

data class PlayerInfo (
    val playerState: UiAudioPlayerState,
    val currentTime: String,
    val currentTrack: TrackInfoDetails,
    val savedPosition: Int
)