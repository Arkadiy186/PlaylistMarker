package com.example.playlistmarker.ui.search.utills.sharing

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.playlistmarker.ui.audioplayer.activity.AudioPlayerActivity
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

class NavigationContractImpl(private val context: Context) : NavigationContract {
    override fun openAudioPlayer(track: TrackInfoDetails) {
        Log.d("Navigation", "Opening AudioPlayerActivity with track: $track")
        val intent = Intent(context, AudioPlayerActivity::class.java).apply {
            putExtra("track", track)
        }
        context.startActivity(intent)
    }
}