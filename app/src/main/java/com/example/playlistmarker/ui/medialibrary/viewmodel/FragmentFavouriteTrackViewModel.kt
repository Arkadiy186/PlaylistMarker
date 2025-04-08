package com.example.playlistmarker.ui.medialibrary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentFavouriteTrackViewModel(trackId: String) : ViewModel() {

    private val _favouriteTrackLiveData = MutableLiveData<String>()
    val favouriteTrackLivedata: LiveData<String> = _favouriteTrackLiveData
}