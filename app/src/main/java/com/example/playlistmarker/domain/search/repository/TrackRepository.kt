package com.example.playlistmarker.domain.search.repository

import com.example.playlistmarker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTrack(query: String): Flow<Resources<List<Track>>>
}