package com.example.playlistmarker.ui.audioplayer.impl

import android.media.MediaPlayer
import com.example.playlistmarker.di.viewModelModule
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.audioplayer.viewmodel.AudioPlayerCallback
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerInteractorImpl : AudioPlayerInteractor {

    private val mediaPlayer = MediaPlayer()
    private var url = ""
    private var currentState: UiAudioPlayerState = UiAudioPlayerState.Default()
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
            currentState = UiAudioPlayerState.Prepared()
            callback?.onPlayerStateChanged(currentState)
        }

        mediaPlayer.setOnCompletionListener {
            currentState = UiAudioPlayerState.Completed()
            callback?.onPlayerStateChanged(currentState)
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        currentState = UiAudioPlayerState.Playing(getCurrentPosition())
        callback?.onPlayerStateChanged(currentState)
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        currentState = UiAudioPlayerState.Paused(getCurrentPosition())
    }

    override fun stopPlayer() {
        //mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        currentState = UiAudioPlayerState.Prepared()
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