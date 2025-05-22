package com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.db.use_cases.TrackDbInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentFavouriteTrackViewModel(private val trackDbInteractor: TrackDbInteractor) : ViewModel() {

    private val _favouriteTrackLiveData = MutableLiveData<FavouriteTracksUiState>()
    val favouriteTrackLivedata: LiveData<FavouriteTracksUiState> = _favouriteTrackLiveData

    init {
        viewModelScope.launch {
            trackDbInteractor.getFavouriteTracks()
                .collectLatest { trackList ->
                    val uiList: List<TrackInfoDetails> = trackList.map { TrackInfoDetailsMapper.map(it) }

                    if (uiList.isEmpty()) {
                        _favouriteTrackLiveData.postValue(FavouriteTracksUiState.Placeholder(message = R.string.media_library_empty))
                    } else {
                        _favouriteTrackLiveData.postValue(FavouriteTracksUiState.Content(tracks = uiList))
                    }
                }
        }
    }
}