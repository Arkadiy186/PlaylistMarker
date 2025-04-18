package com.example.playlistmarker.domain.search.repository

import com.example.playlistmarker.domain.search.model.Track

interface TrackRepository {
    fun searchTrack(query: String): List<Track>
}