package com.example.playlistmarker.ui

import android.app.Application
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.di.dataModule
import com.example.playlistmarker.di.repositoryModule
import com.example.playlistmarker.di.useCasesModule
import com.example.playlistmarker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initialize(applicationContext)

        startKoin {
            androidContext(this@MyApp)
            modules(
                dataModule,
                repositoryModule,
                useCasesModule,
                viewModelModule
            )
        }
    }
}