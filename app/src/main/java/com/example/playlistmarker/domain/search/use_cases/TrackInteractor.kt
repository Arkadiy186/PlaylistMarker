package com.example.playlistmarker.domain.search.use_cases

import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTrack(query: String): Flow<Pair<List<Track>?, String?>>
}