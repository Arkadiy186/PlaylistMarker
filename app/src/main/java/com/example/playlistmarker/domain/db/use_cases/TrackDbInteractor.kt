package com.example.playlistmarker.domain.db.use_cases

import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackDbInteractor {
    suspend fun insertTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getFavouriteTracks(): Flow<List<Track>>
    fun getAllFavouriteTracks(): Flow<List<Int>>
}