package com.example.playlistmarker.di

import com.example.playlistmarker.domain.db.use_cases.TrackDbInteractor
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.domain.player.use_cases.PositionTimeInteractor
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.domain.search.use_cases.NetworkInteractor
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import com.example.playlistmarker.ui.audioplayer.viewmodel.AudioPlayerViewModel
import com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks.FragmentFavouriteTrackViewModel
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.FragmentPlaylistViewModel
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandler
import com.example.playlistmarker.ui.search.viewmodel.historyviewmodel.HistoryViewModel
import com.example.playlistmarker.ui.search.viewmodel.searchviewmodel.SearchViewModel
import com.example.playlistmarker.ui.settings.viewmodel.SettingsViewModel
import org.koin.dsl.module

val viewModelModule = module {
    //ACTIVITY SETTINGS
    factory {
        SettingsViewModel(get<ThemeInteractor>())
    }

    //ACTIVITY SEARCH
    factory {
        HistoryViewModel(get<HistoryInteractor>())
    }

    factory {
        SearchViewModel(get<TrackInteractor>(), get<NetworkInteractor>(), get<DebounceHandler>())
    }

    //ACTIVITY PLAYER
    factory {
        AudioPlayerViewModel(get<AudioPlayerInteractor>(), get<PositionTimeInteractor>(), get<TrackDbInteractor>())
    }

    //ACTIVITY MEDIA LIBRARY
    factory { (playlistId: String) ->
        FragmentPlaylistViewModel(playlistId)
    }

    factory {
        FragmentFavouriteTrackViewModel(get<TrackDbInteractor>())
    }

    //UTILS
    factory<DebounceHandler> {com.example.playlistmarker.creator.Creator.provideDebounceHandler()}
}