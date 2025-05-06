package com.example.playlistmarker.domain.player.use_cases.state

sealed class UiAudioPlayerState(val isPLayButtonClicked: Boolean, val buttonText: String, val progress: Int) {
    class Default() : UiAudioPlayerState(false, "PLAY", 0)

    class Prepared() : UiAudioPlayerState(false, "PLAY", 0)

    class Playing(progress: Int) : UiAudioPlayerState(true, "PAUSE", progress)

    class Paused(progress: Int) : UiAudioPlayerState(true, "PAUSE", progress)
}