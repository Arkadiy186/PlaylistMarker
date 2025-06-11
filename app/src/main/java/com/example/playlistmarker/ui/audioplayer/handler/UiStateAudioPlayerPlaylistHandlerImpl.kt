package com.example.playlistmarker.ui.audioplayer.handler

import android.view.View
import com.example.playlistmarker.databinding.AudioplayerBinding

class UiStateAudioPlayerPlaylistHandlerImpl(private val binding: AudioplayerBinding) : UiStateAudioPlayerPlaylistHandler {
    override fun setPlaceholderVisibility(
        isHidden: Boolean,
        text: String,
        imageRes: Int,
        textRes: Int
    ) {
        if (isHidden) {
            binding.playlistAudioPlayerContent.show()
        } else {
            binding.playlistAudioPlayerContent.gone()
        }
    }

    private fun View.show() {
        visibility = View.VISIBLE
    }

    private fun View.gone() {
        visibility = View.GONE
    }
}