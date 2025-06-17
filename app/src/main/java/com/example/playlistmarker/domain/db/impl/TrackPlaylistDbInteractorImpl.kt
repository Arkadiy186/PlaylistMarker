package com.example.playlistmarker.domain.db.impl

import com.example.playlistmarker.domain.db.model.Track
import com.example.playlistmarker.domain.db.repository.TrackPlaylistDbRepository
import com.example.playlistmarker.domain.db.use_cases.TrackPlaylistDbInteractor
import kotlinx.coroutines.flow.Flow

class TrackPlaylistDbInteractorImpl(private val trackPlaylistDbRepository: TrackPlaylistDbRepository) : TrackPlaylistDbInteractor {
    override suspend fun insertTrack(track: Track, id: Long) {
        trackPlaylistDbRepository.insertTrackIntoPlaylist(track, id)
    }

    override suspend fun deleteTrack(trackId: Long, playlistId: Long) {
        trackPlaylistDbRepository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return trackPlaylistDbRepository.getTracksForPlaylist(playlistId)
    }
}