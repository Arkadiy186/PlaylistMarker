package com.example.playlistmarker.domain.search.use_cases

import com.example.playlistmarker.domain.search.model.Track

interface TrackInteractor {
    fun searchTrack(query: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun onTrackFound(tracks: List<Track>)
        fun onError(error: Throwable)
    }
}