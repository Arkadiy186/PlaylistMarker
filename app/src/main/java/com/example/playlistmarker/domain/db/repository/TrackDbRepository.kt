package com.example.playlistmarker.domain.db.repository

import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackDbRepository {
    suspend fun insertTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getAllFavouriteTrackDetails(): Flow<List<Track>>
    fun getFavouriteTrackIds(): Flow<List<Long>>
}