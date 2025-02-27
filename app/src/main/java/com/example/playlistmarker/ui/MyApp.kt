package com.example.playlistmarker.ui

import android.app.Application
import com.example.playlistmarker.creator.Creator

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initialize(applicationContext)
    }
}