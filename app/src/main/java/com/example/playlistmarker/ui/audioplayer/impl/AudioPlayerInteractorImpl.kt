package com.example.playlistmarker.ui.audioplayer.impl

import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.audioplayer.viewmodel.AudioPlayerCallback
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

class AudioPlayerInteractorImpl : AudioPlayerInteractor {

    private val mediaPlayer = MediaPlayer()
    private var url = ""
    private var currentState: UiAudioPlayerState = UiAudioPlayerState.STATE_DEFAULT
    private var callback: AudioPlayerCallback? = null

    override fun setCallback(callback: AudioPlayerCallback) {
        this.callback = callback
    }

    override fun preparePlayer(track: TrackInfoDetails) {
        url = track.previewUrl

        if (url == "") {
            return
        }

        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        }catch (e:Exception) {
            e.printStackTrace()
        }

        mediaPlayer.setOnPreparedListener {
            currentState = UiAudioPlayerState.STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            Log.d("MediaPlayer", "completed: ${UiAudioPlayerState.STATE_COMPLETED}")
            currentState = UiAudioPlayerState.STATE_COMPLETED
            callback?.onTrackCompletion()
            callback?.onPlayerStateChanged(currentState)
            Log.d("MediaPlayer", "playerState: ${UiAudioPlayerState.STATE_COMPLETED}")
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        currentState = UiAudioPlayerState.STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        currentState = UiAudioPlayerState.STATE_PAUSED
    }

    override fun stopPlayer() {
        mediaPlayer.stop()
        currentState = UiAudioPlayerState.STATE_PREPARED
        mediaPlayer.prepareAsync()
    }

    override fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    override fun getPlayerState(): UiAudioPlayerState {
        return currentState
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}