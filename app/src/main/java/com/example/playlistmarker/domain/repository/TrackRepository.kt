package com.example.playlistmarker.domain.repository

import com.example.playlistmarker.domain.model.Track

interface TrackRepository {
    fun searchTrack(query: String): List<Track>
}