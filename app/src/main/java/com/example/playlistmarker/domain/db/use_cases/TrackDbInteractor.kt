package com.example.playlistmarker.domain.db.use_cases

import com.example.playlistmarker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackDbInteractor {
    fun getFavouriteTracks(): Flow<List<Track>>
}