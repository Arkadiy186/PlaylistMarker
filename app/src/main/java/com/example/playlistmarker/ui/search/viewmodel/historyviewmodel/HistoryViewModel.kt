package com.example.playlistmarker.ui.search.viewmodel.historyviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

class HistoryViewModel(application: Application, private val historyInteractor: HistoryInteractor): AndroidViewModel(application) {

    private val _historyStateLiveData = MutableLiveData<List<TrackInfoDetails>>()
    val historyState: LiveData<List<TrackInfoDetails>> = _historyStateLiveData

    fun loadHistory() {
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