package com.example.playlistmarker.domain.search.use_cases

import com.example.playlistmarker.domain.search.model.Track

interface HistoryInteractor {
    fun addTrackHistory(track: Track)
    suspend fun getTrackHistory(): List<Track>
    fun clearHistory()
    suspend fun loadHistory(consumer: (List<Track>) -> Unit)
}