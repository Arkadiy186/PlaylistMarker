package com.example.playlistmarker.ui.audioplayer.impl

import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.audioplayer.viewmodel.AudioPlayerCallback
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

class AudioPlayerInteractorImpl : AudioPlayerInteractor {

    private var mediaPlayer: MediaPlayer? = null
    private var url = ""
    private var currentState: UiAudioPlayerState = UiAudioPlayerState.Default()
    private var callback: AudioPlayerCallback? = null
    private var autoStartAfterPrepared = false
    private var isPrepared = false
    private var savedPosition = 0
    private var currentTrack: TrackInfoDetails? = null

    override fun setCallback(callback: AudioPlayerCallback) {
        this.callback = callback
    }

    override fun preparePlayer(track: TrackInfoDetails) {
        currentTrack = track
        url = track.previewUrl

        if (url.isBlank()) return

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }

        try {
            mediaPlayer?.reset()
            isPrepared = false
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mediaPlayer?.setOnPreparedListener {
            isPrepared = true
            currentState = UiAudioPlayerState.Prepared()
            callback?.onPlayerStateChanged(currentState)

            if (savedPosition > 0) {
                mediaPlayer?.seekTo(savedPosition)
            }

            if (autoStartAfterPrepared) {
                autoStartAfterPrepared = false
                startPlayer()
            }
        }

        mediaPlayer?.setOnCompletionListener {
            currentState = UiAudioPlayerState.Completed()
            callback?.onPlayerStateChanged(currentState)

            savedPosition = 0
            currentTrack?.let {
                autoStartAfterPrepared = false
                preparePlayer(it)
            }
        }
    }

    override fun startPlayer() {
        mediaPlayer?.let {
            if (currentState is UiAudioPlayerState.Prepared || currentState is UiAudioPlayerState.Paused) {
                it.start()
                currentState = UiAudioPlayerState.Playing(it.currentPosition)
                callback?.onPlayerStateChanged(currentState)
            } else if (currentState is UiAudioPlayerState.Completed) {
                savedPosition = 0
                autoStartAfterPrepared = true
                currentTrack?.let { preparePlayer(it) }
            } else {

            }
        }
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        savedPosition = mediaPlayer?.currentPosition ?: 0
        currentState = UiAudioPlayerState.Paused(savedPosition)
        callback?.onPlayerStateChanged(currentState)
    }

    override fun stopPlayer() {
        mediaPlayer?.let {
            savedPosition = 0
            it.stop()
            currentState = UiAudioPlayerState.Default()
            callback?.onPlayerStateChanged(currentState)
        }
    }

    override fun seekTo(position: Int) {
        mediaPlayer?.let {
            if (currentState is UiAudioPlayerState.Prepared ||
                currentState is UiAudioPlayerState.Playing ||
                currentState is UiAudioPlayerState.Paused
            ) {
                it.seekTo(position)
                savedPosition = position
            }
        }
    }

    override fun getPlayerState(): UiAudioPlayerState {
        return currentState
    }

    override fun getCurrentPositionPlayer(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}