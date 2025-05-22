package com.example.playlistmarker.ui.search.viewmodel.searchviewmodel

import android.provider.Contacts.Intents.UI
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.R
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateData
import com.example.playlistmarker.domain.search.model.Track
import com.example.playlistmarker.domain.search.repository.Resources
import com.example.playlistmarker.domain.search.use_cases.NetworkInteractor
import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandler
import kotlinx.coroutines.launch

class SearchViewModel (
    private val trackInteractor: TrackInteractor,
    private val networkInteractor: NetworkInteractor,
    private val searchStateInteractor: SearchStateInteractor,
    private val debounceHandler: DebounceHandler) : ViewModel() {

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

    private var latestSearchText: String? = null
    private val trackSearchDebounce = debounceHandler.debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { query ->
        searchTrack(query)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    fun searchTrack(query: String) {
        if (query.isEmpty()) return

        if (!networkInteractor.isInternetAvailable()) {
            _uiState.postValue(UiState.ErrorInternet(R.string.internet_problems))
            return
        }

        _uiState.postValue(UiState.Loading(true))


        viewModelScope.launch {
            trackInteractor
                .searchTrack(query)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = foundTracks ?: emptyList()

        when {
            errorMessage != null -> {
                renderState(UiState.ErrorInternet(R.string.internet_problems))
            }
            tracks.isEmpty() -> {
                renderState(UiState.NotFound)
            }
            else -> {
                val trackInfoDetails = tracks.map { TrackInfoDetailsMapper.map(it) }
                _uiState.postValue(UiState.Content(trackInfoDetails))
            }
        }
    }

    private fun renderState(state: UiState) {
        _uiState.postValue(state)
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 3000L
    }
}