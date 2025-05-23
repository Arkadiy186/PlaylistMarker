package com.example.playlistmarker.ui.medialibrary.viewmodel.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentPlaylistViewModel(playlistId: String) : ViewModel() {

    private val _playlistTrackLiveData = MutableLiveData<String>()
    val playlistTrackLivedata: LiveData<String> = _playlistTrackLiveData
}