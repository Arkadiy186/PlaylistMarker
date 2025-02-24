package com.example.playlistmarker.domain.impl

import com.example.playlistmarker.domain.repository.TrackRepository
import com.example.playlistmarker.domain.use_case.TrackInteractor

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {
    override fun searchTrack(query: String, consumer: TrackInteractor.TrackConsumer) {
        val t = Thread {
            val tracks = trackRepository.searchTrack(query)
            consumer.consume(tracks)
        }
        t.start()
    }
}