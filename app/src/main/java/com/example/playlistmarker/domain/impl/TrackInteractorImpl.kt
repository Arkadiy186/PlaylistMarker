package com.example.playlistmarker.domain.impl

import com.example.playlistmarker.domain.repository.TrackRepository
import com.example.playlistmarker.domain.use_case.TrackInteractor
import java.util.concurrent.Executors

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(query: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            try {
                val result = trackRepository.searchTrack(query)
                consumer.onTrackFound(result)
            }catch (e:Exception) {
                consumer.onError(e)
            }
        }
    }
}