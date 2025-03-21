package com.example.playlistmarker.domain.search.use_cases

import com.example.playlistmarker.data.search.sharedpreferences.SearchStateData
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

interface SearchStateInteractor {
    fun saveSearchState(query: String, searchList: List<TrackInfoDetails>, historyList: List<TrackInfoDetails>)
    fun restoreSearchState(): SearchStateData
}