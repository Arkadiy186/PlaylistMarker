package com.example.playlistmarker.domain.impl

import com.example.playlistmarker.domain.model.Track
import com.example.playlistmarker.domain.repository.HistoryRepository
import com.example.playlistmarker.domain.use_case.HistoryInteractor

class HistoryInteractorImpl(private val historyRepository: HistoryRepository) : HistoryInteractor {

    override fun addTrackHistory(track: Track) {
        historyRepository.addTrackHistory(track)
    }

    override fun getTrackHistory(): List<Track> {
        return historyRepository.getHistory()
    }

    override fun clearHistory() {
        return historyRepository.clearHistory()
    }

    override fun loadHistory(consumer: (List<Track>) -> Unit) {
        val history = getTrackHistory()
        consumer(history)
    }

    override fun getTrackByNameAndArtist(trackName: String, artistName: String): List<Track> {
        val history = getTrackHistory()
        return history.filter { track ->
            track.artistName.contains(artistName, ignoreCase = true) &&
                    track.trackName.contains(trackName, ignoreCase = true)
        }
    }
}