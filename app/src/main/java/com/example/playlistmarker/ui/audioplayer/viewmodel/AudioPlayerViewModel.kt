package com.example.playlistmarker.ui.audioplayer.viewmodel

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.domain.player.use_cases.PositionTimeInteractor
import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import java.util.Date
import java.util.Locale

class AudioPlayerViewModel(
    application: Application,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val positionTimeInteractor: PositionTimeInteractor) : AndroidViewModel(application), AudioPlayerCallback {

    private val _playerState = MutableLiveData<UiAudioPlayerState>().apply { value = UiAudioPlayerState.STATE_DEFAULT }
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

    init {
        audioPlayerInteractor.setCallback(this)

        val update = {
            _playerInfo.value = PlayerInfo(
                _playerState.value ?: UiAudioPlayerState.STATE_DEFAULT,
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

        if (state == UiAudioPlayerState.STATE_PLAYING) {
            mainHandlerThread.post(updateUiTimer)
        } else {
            mainHandlerThread.removeCallbacks(updateUiTimer)
        }
    }

    override fun onTrackCompletion() {
        _playerState.postValue(UiAudioPlayerState.STATE_COMPLETED)
    }

    private val mainHandlerThread = Handler(Looper.getMainLooper())
    private val updateUiTimer = object : Runnable {
        override fun run() {
            if (audioPlayerInteractor.getPlayerState() == UiAudioPlayerState.STATE_PLAYING) {
                val currentPosition = audioPlayerInteractor.getCurrentPosition()
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(currentPosition.toLong()))
                _currentTime.postValue(formattedTime)
                mainHandlerThread.removeCallbacks(this)
                mainHandlerThread.postDelayed(this, 400)
            }
        }
    }

    fun prepareTrack(track: TrackInfoDetails) {
        _currentTrack.value = track
        audioPlayerInteractor.preparePlayer(track)
        _playerState.postValue(UiAudioPlayerState.STATE_PREPARED)
    }

    fun playTrack() {
        if (_playerState.value == UiAudioPlayerState.STATE_PLAYING) return
        loadSavePosition().let {
            audioPlayerInteractor.seekTo(it)
        }
        audioPlayerInteractor.startPlayer()
        _playerState.postValue(UiAudioPlayerState.STATE_PLAYING)
        mainHandlerThread.post(updateUiTimer)
    }

    fun pauseTrack() {
        savePosition()
        audioPlayerInteractor.pausePlayer()
        _playerState.postValue(UiAudioPlayerState.STATE_PAUSED)
        mainHandlerThread.removeCallbacks(updateUiTimer)
    }

    fun stopTrack() {
        savePosition()
        audioPlayerInteractor.stopPlayer()
        _playerState.postValue(UiAudioPlayerState.STATE_PREPARED)
        mainHandlerThread.removeCallbacks(updateUiTimer)
    }

    fun resetTrackTime() {
        positionTimeInteractor.resetPosition()
        audioPlayerInteractor.stopPlayer()
        _currentTime.postValue("00:00")
        Log.d("MediaPlayer", "completed")
        _playerState.postValue(UiAudioPlayerState.STATE_COMPLETED)
        mainHandlerThread.removeCallbacks(updateUiTimer)
    }

    fun savePosition() {
        val currentPosition = audioPlayerInteractor.getCurrentPosition()
        positionTimeInteractor.saveCurrentPosition(currentPosition)
    }

    fun seekTo(position: Int) {
        audioPlayerInteractor.seekTo(position)
    }

    private fun loadSavePosition(): Int {
        val currentPosition = positionTimeInteractor.getCurrentPosition()
        _savedPosition.postValue(currentPosition)
        return currentPosition
    }
}

data class PlayerInfo (
    val playerState: UiAudioPlayerState,
    val currentTime: String,
    val currentTrack: TrackInfoDetails,
    val savedPosition: Int
)