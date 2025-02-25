package com.example.playlistmarker.domain.impl

import com.example.playlistmarker.domain.use_case.SearchStateInteractor
import com.example.playlistmarker.presentation.model.TrackInfo
import com.example.playlistmarker.presentation.utills.SearchStateManager

class SearchStateInteractorImpl(private val searchStateManager: SearchStateManager) : SearchStateInteractor {
    override fun saveSearchState(query: String, searchList: List<TrackInfo>, historyList: List<TrackInfo>) {
        searchStateManager.saveSearchState(query, searchList, historyList)
    }

    override fun restoreSearchState(callback: (String, List<TrackInfo>, List<TrackInfo>) -> Unit) {
        return searchStateManager.restoreSearchState(callback)
    }
}