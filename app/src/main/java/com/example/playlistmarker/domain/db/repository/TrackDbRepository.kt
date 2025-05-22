package com.example.playlistmarker.domain.db.repository

import com.example.playlistmarker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackDbRepository {
    suspend fun insertTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getFavouriteTracks(): Flow<List<Track>>
}