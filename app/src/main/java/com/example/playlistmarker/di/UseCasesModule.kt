package com.example.playlistmarker.di

import com.example.playlistmarker.domain.search.impl.HistoryInteractorImpl
import com.example.playlistmarker.domain.search.impl.NetworkInteractorImpl
import com.example.playlistmarker.domain.search.impl.SearchStateInteractorImpl
import com.example.playlistmarker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmarker.domain.search.repository.HistoryRepository
import com.example.playlistmarker.domain.search.repository.TrackRepository
import com.example.playlistmarker.domain.search.repository.utills.NetworkRepository
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.domain.search.use_cases.NetworkInteractor
import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
import com.example.playlistmarker.domain.settings.impl.ThemeInteractorImpl
import com.example.playlistmarker.domain.settings.repository.ThemeRepository
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateManager
import com.example.playlistmarker.domain.db.impl.TrackDbInteractorImpl
import com.example.playlistmarker.domain.db.repository.TrackDbRepository
import com.example.playlistmarker.domain.db.use_cases.TrackDbInteractor
import com.example.playlistmarker.domain.player.impl.PositionTimeInteractorImpl
import com.example.playlistmarker.domain.player.repository.PositionTimeRepository
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.domain.player.use_cases.PositionTimeInteractor
import com.example.playlistmarker.domain.search.repository.SearchStateRepository
import com.example.playlistmarker.ui.audioplayer.impl.AudioPlayerInteractorImpl
import org.koin.dsl.module

val useCasesModule = module {
    //ACTIVITY SETTINGS, MAIN
    factory<ThemeInteractor> {
        ThemeInteractorImpl(get<ThemeRepository>())
    }

    //ACTIVITY SEARCH
    factory<HistoryInteractor> {
        HistoryInteractorImpl(get<HistoryRepository>())
    }

    factory<NetworkInteractor> {
        NetworkInteractorImpl(get<NetworkRepository>())
    }

    factory<TrackInteractor> {
        TrackInteractorImpl(get<TrackRepository>())
    }

    factory<SearchStateInteractor> {
        SearchStateInteractorImpl(get<SearchStateRepository>())
    }

    //ACTIVITY PLAYER
    factory<PositionTimeInteractor> {
        PositionTimeInteractorImpl(get<PositionTimeRepository>())
    }

    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl()
    }

    //DATABASE
    factory<TrackDbInteractor> {
        TrackDbInteractorImpl(get<TrackDbRepository>())
    }
}