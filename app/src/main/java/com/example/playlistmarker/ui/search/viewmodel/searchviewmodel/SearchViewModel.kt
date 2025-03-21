package com.example.playlistmarker.ui.search.viewmodel.searchviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateData
import com.example.playlistmarker.domain.search.model.Track
import com.example.playlistmarker.domain.search.use_cases.NetworkInteractor
import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper

class SearchViewModel (
    application: Application,
    private val trackInteractor: TrackInteractor,
    private val networkInteractor: NetworkInteractor,
    private val searchStateInteractor: SearchStateInteractor) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _searchState = MutableLiveData<SearchStateData>()
    val searchState: LiveData<SearchStateData> = _searchState

    private val _combinedLiveData = MediatorLiveData<Pair<UiState?, SearchStateData?>>()
    val combinedLiveData: LiveData<Pair<UiState?, SearchStateData?>> = _combinedLiveData

    init {
        _combinedLiveData.addSource(uiState) {uiStateValue ->
            _combinedLiveData.value = Pair(uiStateValue, searchState.value)
        }

        _combinedLiveData.addSource(searchState) { searchStateValue ->
            _combinedLiveData.value = Pair(uiState.value, searchStateValue)
        }
    }

    fun searchTrack(query: String) {
        if (query.isEmpty()) return

        if (!networkInteractor.isInternetAvailable()) {
            _uiState.postValue(UiState.ErrorInternet(R.string.internet_problems))
            return
        }

        _uiState.postValue(UiState.Loading(true))

        trackInteractor.searchTrack(query, object : TrackInteractor.TrackConsumer {
            override fun onTrackFound(tracks: List<Track>) {
                _uiState.postValue(UiState.Loading(false))

                if (tracks.isEmpty()) {
                    _uiState.postValue(UiState.NotFound)
                } else {
                    val trackInfoDetails = tracks.map { TrackInfoDetailsMapper.map(it) }
                    _uiState.postValue(UiState.Content(trackInfoDetails))
                }
            }

            override fun onError(error: Throwable) {
                _uiState.postValue(UiState.Loading(false))
                if (!networkInteractor.isInternetAvailable()) {
                    _uiState.postValue(UiState.ErrorInternet(R.string.internet_problems))
                }else {
                    _uiState.postValue(UiState.NotFound)
                }
            }
        })
    }

    fun restoreSearchState() {
        val restoreStateData = searchStateInteractor.restoreSearchState()
        _searchState.postValue(restoreStateData)
    }
}