package com.example.playlistmarker.domain.search.repository

import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTrack(query: String): Flow<Resources<List<Track>>>
    suspend fun insertTrack(track: Track)
}