package com.example.playlistmarker.domain.db.use_cases

import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackPlaylistDbInteractor {
    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>
    suspend fun insertTrack(track: Track, id: Long)
    suspend fun deleteTrack(trackId: Long, playlistId: Long)
}