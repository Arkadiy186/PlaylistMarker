package com.example.playlistmarker.data.search.impl

import com.example.playlistmarker.data.db.TrackDatabase
import com.example.playlistmarker.data.mappers.TrackDtoMapper
import com.example.playlistmarker.data.search.sharedpreferences.HistorySearch
import com.example.playlistmarker.domain.db.model.Track
import com.example.playlistmarker.domain.search.repository.HistoryRepository
import kotlinx.coroutines.flow.first

class HistoryRepositoryImpl(
    private val historySearch: HistorySearch,
    private val appDatabase: TrackDatabase
) : HistoryRepository {

    override fun addTrackHistory(track: Track) {
        val trackDto = TrackDtoMapper.toDto(track)
        historySearch.addTrackHistory(trackDto)
    }

    override suspend fun getHistory(): List<Track> {
        val favouriteIds = appDatabase.trackDao().getIdTracks().first()
        val trackDtoList = historySearch.getHistory()
        val mapDomainTrack = trackDtoList.map { TrackDtoMapper.mapToDomain(it) }
        val updateTracks = mapDomainTrack.map { track ->
            track.copy(isFavourite = favouriteIds.contains(track.id))
        }
        return updateTracks
    }

    override fun clearHistory() {
        historySearch.clearHistory()
    }
}