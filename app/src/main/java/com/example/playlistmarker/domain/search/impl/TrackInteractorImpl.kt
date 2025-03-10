package com.example.playlistmarker.domain.search.impl

import com.example.playlistmarker.domain.search.repository.TrackRepository
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
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