package com.example.playlistmarker.ui.search.viewmodel.searchviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.domain.search.model.Track
import com.example.playlistmarker.domain.search.use_cases.NetworkInteractor
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper

class SearchViewModel (application: Application) : AndroidViewModel(application) {

    private val trackInteractor: TrackInteractor by lazy { Creator.provideTrackInteractor() }
    private val networkInteractor: NetworkInteractor by lazy { Creator.provideNetworkInteractor() }

    private val _uiState = MutableLiveData<UiState>()
    val searchState: LiveData<UiState> = _uiState

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
}