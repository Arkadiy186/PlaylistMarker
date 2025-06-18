package com.example.playlistmarker.domain.db.repository

import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackPlaylistDbRepository {
    suspend fun insertTrackIntoPlaylist(track: Track, id: Long)
    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long)
    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>
}