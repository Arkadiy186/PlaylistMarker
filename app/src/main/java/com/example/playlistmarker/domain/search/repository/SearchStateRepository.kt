package com.example.playlistmarker.domain.search.repository

import com.example.playlistmarker.data.search.sharedpreferences.SearchStateData
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

interface SearchStateRepository {
    fun saveSearchState(searchText: String, searchList: List<TrackInfoDetails>, historyList: List<TrackInfoDetails>)
    fun restoreSearchState(): SearchStateData
}