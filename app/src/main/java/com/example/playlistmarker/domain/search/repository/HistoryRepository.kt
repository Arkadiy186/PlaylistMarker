package com.example.playlistmarker.domain.search.repository

import com.example.playlistmarker.domain.db.model.Track

interface HistoryRepository {
    fun addTrackHistory(track: Track)
    suspend fun getHistory(): List<Track>
    fun clearHistory()
}