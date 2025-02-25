package com.example.playlistmarker.domain.use_case

import com.example.playlistmarker.presentation.model.TrackInfo

interface SearchStateInteractor {
    fun saveSearchState(query: String, searchList: List<TrackInfo>, historyList: List<TrackInfo>)
    fun restoreSearchState(callback: (String, List<TrackInfo>, List<TrackInfo>) -> Unit)
}