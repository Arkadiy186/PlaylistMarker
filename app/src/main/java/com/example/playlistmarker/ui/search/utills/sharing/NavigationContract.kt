package com.example.playlistmarker.ui.search.utills.sharing

import com.example.playlistmarker.ui.search.model.TrackInfoDetails

interface NavigationContract {
    fun openAudioPlayer(track: TrackInfoDetails)
}