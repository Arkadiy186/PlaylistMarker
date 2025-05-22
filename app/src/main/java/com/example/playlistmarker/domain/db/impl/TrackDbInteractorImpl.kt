package com.example.playlistmarker.domain.db.impl

import com.example.playlistmarker.domain.db.repository.TrackDbRepository
import com.example.playlistmarker.domain.db.use_cases.TrackDbInteractor
import com.example.playlistmarker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackDbInteractorImpl(private val trackDbRepository: TrackDbRepository) : TrackDbInteractor {
    override fun getFavouriteTracks(): Flow<List<Track>> {
        return trackDbRepository.getFavouriteTracks()
            .map { tracks -> sortReverseOrder(tracks) }
    }

    private fun sortReverseOrder(tracks: List<Track>): List<Track> {
        return tracks.sortedByDescending { it.id }
    }
}