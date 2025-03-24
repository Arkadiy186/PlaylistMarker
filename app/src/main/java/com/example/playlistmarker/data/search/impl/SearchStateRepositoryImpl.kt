package com.example.playlistmarker.data.search.impl

import com.example.playlistmarker.data.search.sharedpreferences.SearchStateData
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateManager
import com.example.playlistmarker.domain.search.repository.SearchStateRepository
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

class SearchStateRepositoryImpl(private val searchStateManager: SearchStateManager) : SearchStateRepository {
    override fun saveSearchState(
        searchText: String,
        searchList: List<TrackInfoDetails>,
        historyList: List<TrackInfoDetails>
    ) {
        searchStateManager.saveSearchState(searchText, searchList, historyList)
    }

    override fun restoreSearchState(): SearchStateData {
        return searchStateManager.restoreSearchState()
    }
}