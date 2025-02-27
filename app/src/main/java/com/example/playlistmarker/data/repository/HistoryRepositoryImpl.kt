package com.example.playlistmarker.data.repository

import com.example.playlistmarker.data.mappers.TrackDtoMapper
import com.example.playlistmarker.data.sharedpreferences.HistorySearch
import com.example.playlistmarker.domain.model.Track
import com.example.playlistmarker.domain.repository.HistoryRepository

class HistoryRepositoryImpl(private val historySearch: HistorySearch) : HistoryRepository {

    override fun addTrackHistory(track: Track) {
        val trackDto = TrackDtoMapper.toDto(track)
        historySearch.addTrackHistory(trackDto)
    }

    override fun getHistory(): List<Track> {
        val trackDtoList = historySearch.getHistory()
        return trackDtoList.map { TrackDtoMapper.mapToDomain(it) }
    }

    override fun clearHistory() {
        historySearch.clearHistory()
    }
}