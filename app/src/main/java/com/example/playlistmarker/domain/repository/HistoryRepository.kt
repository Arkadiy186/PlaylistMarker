package com.example.playlistmarker.domain.repository

import com.example.playlistmarker.domain.model.Track

interface HistoryRepository {
    fun addTrackHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}