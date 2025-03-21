package com.example.playlistmarker.di

import android.app.Application
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.domain.search.use_cases.NetworkInteractor
import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import com.example.playlistmarker.ui.search.viewmodel.historyviewmodel.HistoryViewModel
import com.example.playlistmarker.ui.search.viewmodel.searchviewmodel.SearchViewModel
import com.example.playlistmarker.ui.settings.viewmodel.SettingsViewModel
import org.koin.dsl.module

val viewModelModule = module {
    //ACTIVITY SETTINGS
    factory {
        SettingsViewModel(get<Application>(), get<ThemeInteractor>())
    }

    //ACTIVITY SEARCH
    factory {
        HistoryViewModel(get<Application>(), get<HistoryInteractor>())
    }

    factory {
        SearchViewModel(get<Application>(), get<TrackInteractor>(), get<NetworkInteractor>(), get<SearchStateInteractor>())
    }
}