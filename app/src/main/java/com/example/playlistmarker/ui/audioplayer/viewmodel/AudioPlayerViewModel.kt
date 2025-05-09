package com.example.playlistmarker.ui.audioplayer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.domain.player.use_cases.PositionTimeInteractor
import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel (
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val positionTimeInteractor: PositionTimeInteractor) : ViewModel(), AudioPlayerCallback {

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

    val track = TrackInfoDetails(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "")

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
    }

    override fun onPlayerStateChanged(state: UiAudioPlayerState) {
        _playerState.postValue(state)

        when(state) {
            is UiAudioPlayerState.Playing -> startTimer()
            is UiAudioPlayerState.Completed -> {
                stopTimer()
                _currentTime.postValue("00:00")
                _savedPosition.postValue(0)
                _currentTrack.value?.let {
                    audioPlayerInteractor.preparePlayer(it)
                }
            }
            else -> stopTimer()
        }
    }

    fun prepareTrack(track: TrackInfoDetails) {
        _currentTrack.value = track
        audioPlayerInteractor.preparePlayer(track)
    }

    fun playTrack() {
        val playerState = audioPlayerInteractor.getPlayerState()

        if (playerState is UiAudioPlayerState.Playing) return

        val savedPosition = audioPlayerInteractor.getCurrentPosition()
        audioPlayerInteractor.seekTo(savedPosition)
        _savedPosition.postValue(currentPosition)

        when(playerState) {
            is UiAudioPlayerState.Prepared,
                is UiAudioPlayerState.Paused,
                     is UiAudioPlayerState.Completed -> {
                    audioPlayerInteractor.startPlayer()
                    updateStateAsPlaying()
                }
            else -> {
                Log.e("AudioPlayer", "Invalid state to start playback: $playerState")
            }
        }
    }

    fun pauseTrack() {
        savePosition()
        audioPlayerInteractor.pausePlayer()
        updateStateAsPaused()
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

    private fun updateStateAsPlaying() {
        currentPosition = audioPlayerInteractor.getCurrentPosition()
        _playerState.postValue(UiAudioPlayerState.Playing(currentPosition))
    }

    private fun updateStateAsPaused() {
        currentPosition = audioPlayerInteractor.getCurrentPosition()
        _playerState.postValue(UiAudioPlayerState.Paused(currentPosition))
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

                currentPosition = audioPlayerInteractor.getCurrentPosition()
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