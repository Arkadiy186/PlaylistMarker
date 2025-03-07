package com.example.playlistmarker.domain.player.impl

import android.media.MediaPlayer
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerCallback
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

class AudioPlayerInteractorImpl(private val mediaPlayer: MediaPlayer) : AudioPlayerInteractor {

    private var playerState = STATE_DEFAULT
    private var callback: AudioPlayerCallback? = null
    private var url = ""

    override fun preparePlayer(track: TrackInfoDetails) {
        url = track.previewUrl

        if (url == "") {
            callback?.onPlayerCompleted()
            return
        }

        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        }catch (e:Exception) {
            e.printStackTrace()
            callback?.onPlayerCompleted()
        }

        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            callback?.onPlayerPrepared()
        }

        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            callback?.onPlayerCompleted()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    override fun getPlayerState(): Int {
        return playerState
    }

    override fun setCallback(callback: AudioPlayerCallback) {
        this.callback = callback
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}