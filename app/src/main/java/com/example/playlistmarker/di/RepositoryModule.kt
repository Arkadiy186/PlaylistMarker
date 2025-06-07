package com.example.playlistmarker.di

import android.content.Context
import com.example.playlistmarker.data.db.PlaylistDataBase
import com.example.playlistmarker.data.db.TrackDatabase
import com.example.playlistmarker.data.db.converters.PlaylistDbConverter
import com.example.playlistmarker.data.db.converters.TrackDbConverter
import com.example.playlistmarker.data.db.impl.PlaylistDbRepositoryImpl
import com.example.playlistmarker.data.db.impl.TrackDbRepositoryImpl
import com.example.playlistmarker.data.player.impl.PositionTimeRepositoryImpl
import com.example.playlistmarker.data.player.sharedpreferences.PositionTime
import com.example.playlistmarker.data.search.impl.HistoryRepositoryImpl
import com.example.playlistmarker.data.search.impl.SearchStateRepositoryImpl
import com.example.playlistmarker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmarker.data.search.impl.utills.NetworkRepositoryImpl
import com.example.playlistmarker.data.search.network.RetrofitClient
import com.example.playlistmarker.data.db.sharedpreferences.AddedAtStorage
import com.example.playlistmarker.data.search.sharedpreferences.HistorySearch
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateManager
import com.example.playlistmarker.data.settings.impl.ThemeRepositoryImpl
import com.example.playlistmarker.data.settings.sharedpreferences.ThemePreferences
import com.example.playlistmarker.domain.db.repository.PlaylistDbRepository
import com.example.playlistmarker.domain.db.repository.TrackDbRepository
import com.example.playlistmarker.domain.player.repository.PositionTimeRepository
import com.example.playlistmarker.domain.search.repository.HistoryRepository
import com.example.playlistmarker.domain.search.repository.SearchStateRepository
import com.example.playlistmarker.domain.search.repository.TrackRepository
import com.example.playlistmarker.domain.search.repository.utills.NetworkRepository
import com.example.playlistmarker.domain.settings.repository.ThemeRepository
import org.koin.dsl.module

val repositoryModule = module {
    //ACTIVITY SETTINGS
    single<ThemeRepository> {
        ThemeRepositoryImpl(get<ThemePreferences>())
    }

    //ACTIVITY SEARCH
    single<HistoryRepository> {
        HistoryRepositoryImpl(get<HistorySearch>(),get<TrackDatabase>())
    }

    single<NetworkRepository> {
        NetworkRepositoryImpl(get<Context>())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(get<RetrofitClient>(),get<TrackDatabase>(), get<AddedAtStorage>())
    }

    single<SearchStateRepository> {
        SearchStateRepositoryImpl(get<SearchStateManager>())
    }

    //ACTIVITY PLAYER
    single<PositionTimeRepository> {
        PositionTimeRepositoryImpl(get<PositionTime>())
    }

    //DATABASE
    factory { TrackDbConverter() }

    single<TrackDbRepository> {
        TrackDbRepositoryImpl(get<TrackDatabase>(),get<TrackDbConverter>(), get<AddedAtStorage>())
    }

    factory { PlaylistDbConverter() }

    single<PlaylistDbRepository> {
        PlaylistDbRepositoryImpl(get<PlaylistDataBase>(), get<PlaylistDbConverter>())
    }
}