package com.example.playlistmarker.domain.use_case

import com.example.playlistmarker.presentation.model.TrackInfoDetails

interface SearchStateInteractor {
    fun saveSearchState(query: String, searchList: List<TrackInfoDetails>, historyList: List<TrackInfoDetails>)
    fun restoreSearchState(callback: (String, List<TrackInfoDetails>, List<TrackInfoDetails>) -> Unit)
}