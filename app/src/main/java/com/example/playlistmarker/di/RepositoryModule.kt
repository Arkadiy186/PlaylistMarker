package com.example.playlistmarker.di

import android.content.Context
import com.example.playlistmarker.data.player.impl.PositionTimeRepositoryImpl
import com.example.playlistmarker.data.player.sharedpreferences.PositionTime
import com.example.playlistmarker.data.search.impl.HistoryRepositoryImpl
import com.example.playlistmarker.data.search.impl.SearchStateRepositoryImpl
import com.example.playlistmarker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmarker.data.search.impl.utills.NetworkRepositoryImpl
import com.example.playlistmarker.data.search.network.RetrofitClient
import com.example.playlistmarker.data.search.sharedpreferences.HistorySearch
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateManager
import com.example.playlistmarker.data.settings.impl.ThemeRepositoryImpl
import com.example.playlistmarker.data.settings.sharedpreferences.ThemePreferences
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
        HistoryRepositoryImpl(get<HistorySearch>())
    }

    single<NetworkRepository> {
        NetworkRepositoryImpl(get<Context>())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(get<RetrofitClient>())
    }

    single<SearchStateRepository> {
        SearchStateRepositoryImpl(get<SearchStateManager>())
    }

    //ACTIVITY PLAYER
    single<PositionTimeRepository> {
        PositionTimeRepositoryImpl(get<PositionTime>())
    }
}