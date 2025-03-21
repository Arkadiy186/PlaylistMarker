package com.example.playlistmarker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmarker.data.search.network.RetrofitClient
import com.example.playlistmarker.data.search.network.RetrofitClientImpl
import com.example.playlistmarker.data.search.network.TrackAPI
import com.example.playlistmarker.data.search.sharedpreferences.HistorySearch
import com.example.playlistmarker.data.settings.sharedpreferences.ThemePreferences
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    //ACTIVITY SETTINGS
    single<SharedPreferences>(named("theme_prefs")) {
        androidContext()
            .getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    }

    single<ThemePreferences> {
        ThemePreferences(get<SharedPreferences>(named("theme_prefs")))
    }

    //ACTIVITY SEARCH
    single<SharedPreferences>(named("search_prefs")) {
        androidContext()
            .getSharedPreferences("search_name_prefs", Context.MODE_PRIVATE)
    }

    single {
        HistorySearch(get<SharedPreferences>(named("search_prefs")))
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<TrackAPI> {
        get<Retrofit>().create(TrackAPI::class.java)
    }

    single<RetrofitClient> {
        RetrofitClientImpl(get<TrackAPI>())
    }

    single {
        SearchStateManager(get<SharedPreferences>(named("search_prefs")))
    }
}