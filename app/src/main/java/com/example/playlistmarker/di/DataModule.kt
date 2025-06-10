package com.example.playlistmarker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmarker.data.db.PlaylistDataBase
import com.example.playlistmarker.data.db.FavouriteTrackDatabase
import com.example.playlistmarker.data.db.PlaylistTrackDatabase
import com.example.playlistmarker.data.db.sharedpreferences.AddedAtStorage
import com.example.playlistmarker.data.player.sharedpreferences.PositionTime
import com.example.playlistmarker.data.search.network.RetrofitClient
import com.example.playlistmarker.data.search.network.RetrofitClientImpl
import com.example.playlistmarker.data.search.network.TrackAPI
import com.example.playlistmarker.data.search.sharedpreferences.HistorySearch
import com.example.playlistmarker.data.settings.sharedpreferences.ThemePreferences
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateManager
import com.example.playlistmarker.domain.search.repository.utills.NetworkRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.sin

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
        RetrofitClientImpl(get<TrackAPI>(), get<NetworkRepository>())
    }

    single {
        SearchStateManager(get<SharedPreferences>(named("search_prefs")))
    }

    //ACTIVITY PLAYER
    single<SharedPreferences>(named("player_prefs")) {
        androidContext()
            .getSharedPreferences("player_position_prefs", Context.MODE_PRIVATE)
    }

    single {
        PositionTime(get<SharedPreferences>(named("player_prefs")))
    }

    //DATABASE
    single<FavouriteTrackDatabase> {
        Room.databaseBuilder(androidContext(), FavouriteTrackDatabase::class.java, "TrackDatabase.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single<PlaylistDataBase> {
        Room.databaseBuilder(androidContext(), PlaylistDataBase::class.java, "PlaylistDatabase.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single<PlaylistTrackDatabase> {
        Room.databaseBuilder(androidContext(), PlaylistTrackDatabase::class.java, "PlaylistTrackDatabase.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // ADDED_AT storage
    single<SharedPreferences>(named("added_at_prefs")) {
        androidContext().getSharedPreferences("added_at_prefs", Context.MODE_PRIVATE)
    }

    single {
        AddedAtStorage(get<SharedPreferences>(named("added_at_prefs")))
    }
}