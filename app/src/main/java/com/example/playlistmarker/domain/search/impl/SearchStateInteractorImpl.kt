package com.example.playlistmarker.domain.search.impl

import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.example.playlistmarker.ui.search.utills.statemanager.SearchStateManager

class SearchStateInteractorImpl(private val searchStateManager: SearchStateManager) :
    SearchStateInteractor {
    override fun saveSearchState(query: String, searchList: List<TrackInfoDetails>, historyList: List<TrackInfoDetails>) {
        searchStateManager.saveSearchState(query, searchList, historyList)
    }

    override fun restoreSearchState(callback: (String, List<TrackInfoDetails>, List<TrackInfoDetails>) -> Unit) {
        return searchStateManager.restoreSearchState(callback)
    }
}