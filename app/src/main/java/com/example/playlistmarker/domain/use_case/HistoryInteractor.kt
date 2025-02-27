package com.example.playlistmarker.domain.use_case

import com.example.playlistmarker.domain.model.Track

interface HistoryInteractor {
    fun addTrackHistory(track: Track)
    fun getTrackHistory(): List<Track>
    fun clearHistory()
    fun loadHistory(consumer: (List<Track>) -> Unit)
    fun getTrackByNameAndArtist(trackName: String, artistName: String): List<Track>
}