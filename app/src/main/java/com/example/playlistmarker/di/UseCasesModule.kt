package com.example.playlistmarker.di

import com.example.playlistmarker.domain.settings.impl.ThemeInteractorImpl
import com.example.playlistmarker.domain.settings.repository.ThemeRepository
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import org.koin.dsl.module

val useCasesModule = module {
    //ACTIVITY SETTINGS, MAIN
    factory<ThemeInteractor> {
        ThemeInteractorImpl(get<ThemeRepository>())
    }
}