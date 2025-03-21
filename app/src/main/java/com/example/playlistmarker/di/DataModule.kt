package com.example.playlistmarker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmarker.data.settings.sharedpreferences.ThemePreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    //ACTIVITY SETTINGS
    single<SharedPreferences>(named("theme_prefs")) {
        androidContext()
            .getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    }

    single<ThemePreferences> {
        ThemePreferences(get<SharedPreferences>(named("theme_prefs")))
    }
}