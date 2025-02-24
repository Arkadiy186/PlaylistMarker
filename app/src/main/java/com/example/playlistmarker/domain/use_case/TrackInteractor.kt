package com.example.playlistmarker.domain.use_case

import com.example.playlistmarker.domain.model.Track

interface TrackInteractor {
    fun searchTrack(query: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
    }
}