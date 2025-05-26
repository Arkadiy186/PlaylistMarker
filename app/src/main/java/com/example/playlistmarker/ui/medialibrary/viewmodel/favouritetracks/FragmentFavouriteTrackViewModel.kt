package com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.db.use_cases.TrackDbInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FragmentFavouriteTrackViewModel(private val trackDbInteractor: TrackDbInteractor) : ViewModel() {

    private val _favouriteTrackLiveData = MutableStateFlow<FavouriteTracksUiState>(FavouriteTracksUiState.Placeholder(R.string.media_library_empty))
    val favouriteTrackLivedata: StateFlow<FavouriteTracksUiState> = _favouriteTrackLiveData.asStateFlow()

    fun refreshFavourites() {
        viewModelScope.launch {
            trackDbInteractor.getFavouriteTracks()
                .collect { trackList ->
                    val uiList = trackList.map { TrackInfoDetailsMapper.map(it) }

                    _favouriteTrackLiveData.value = if (uiList.isEmpty()) {
                        FavouriteTracksUiState.Placeholder(R.string.media_library_empty)
                    } else {
                        FavouriteTracksUiState.Content(uiList)
                    }
                }
        }
    }
}