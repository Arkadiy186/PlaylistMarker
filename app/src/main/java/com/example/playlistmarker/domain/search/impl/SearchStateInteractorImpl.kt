package com.example.playlistmarker.domain.search.impl

import com.example.playlistmarker.data.search.sharedpreferences.SearchStateData
import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateManager
import com.example.playlistmarker.domain.search.repository.SearchStateRepository

class SearchStateInteractorImpl(private val searchStateRepository: SearchStateRepository) : SearchStateInteractor {
    override fun saveSearchState(query: String, searchList: List<TrackInfoDetails>, historyList: List<TrackInfoDetails>) {
        searchStateRepository.saveSearchState(query, searchList, historyList)
    }

    override fun restoreSearchState(): SearchStateData {
        return searchStateRepository.restoreSearchState()
    }
}