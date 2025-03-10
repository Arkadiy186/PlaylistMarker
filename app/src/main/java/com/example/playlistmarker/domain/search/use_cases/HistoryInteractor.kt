package com.example.playlistmarker.domain.search.use_cases

import com.example.playlistmarker.domain.search.model.Track

interface HistoryInteractor {
    fun addTrackHistory(track: Track)
    fun getTrackHistory(): List<Track>
    fun clearHistory()
    fun loadHistory(consumer: (List<Track>) -> Unit)
    fun getTrackByNameAndArtist(trackName: String, artistName: String): List<Track>
}