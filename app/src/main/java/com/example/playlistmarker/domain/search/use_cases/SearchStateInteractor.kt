package com.example.playlistmarker.domain.search.use_cases

import com.example.playlistmarker.ui.search.model.TrackInfoDetails

interface SearchStateInteractor {
    fun saveSearchState(query: String, searchList: List<TrackInfoDetails>, historyList: List<TrackInfoDetails>)
    fun restoreSearchState(callback: (String, List<TrackInfoDetails>, List<TrackInfoDetails>) -> Unit)
}