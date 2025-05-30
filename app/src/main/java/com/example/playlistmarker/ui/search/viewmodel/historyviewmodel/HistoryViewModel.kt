package com.example.playlistmarker.ui.search.viewmodel.historyviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

class HistoryViewModel(private val historyInteractor: HistoryInteractor): ViewModel() {

    private val _historyStateLiveData = MutableLiveData<List<TrackInfoDetails>>()
    val historyState: LiveData<List<TrackInfoDetails>> = _historyStateLiveData

    suspend fun loadHistory() {
        historyInteractor.loadHistory { history ->
            val trackInfo = history.map { TrackInfoDetailsMapper.map(it) }
            _historyStateLiveData.postValue(trackInfo)
        }
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _historyStateLiveData.postValue(emptyList())
    }
}