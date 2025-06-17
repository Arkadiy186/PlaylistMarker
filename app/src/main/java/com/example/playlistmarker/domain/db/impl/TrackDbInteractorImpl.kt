package com.example.playlistmarker.domain.db.impl

import com.example.playlistmarker.domain.db.repository.TrackDbRepository
import com.example.playlistmarker.domain.db.use_cases.TrackDbInteractor
import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackDbInteractorImpl(private val trackDbRepository: TrackDbRepository) : TrackDbInteractor {

    override suspend fun insertTrack(track: Track) {
        trackDbRepository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        trackDbRepository.deleteTrack(track)
    }

    override fun getAllFavouriteTrackDetails(): Flow<List<Track>> {
        return trackDbRepository.getAllFavouriteTrackDetails()
            .map { tracks ->
                tracks.sortedByDescending { it.addedAt } }
    }

    override fun getFavouriteTrackIds(): Flow<List<Long>> {
        return trackDbRepository.getFavouriteTrackIds()
    }
}