package com.example.playlistmarker.ui.main.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import com.example.playlistmarker.ui.settings.activity.SettingsActivity
import com.example.playlistmarker.ui.medialibrary.activity.MediaLibraryActivity
import com.example.playlistmarker.ui.search.activity.SearchActivity
import com.google.android.material.button.MaterialButton
import org.koin.android.ext.android.inject

class MainActivity() : AppCompatActivity() {

    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isDarkTheme = themeInteractor.getTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.activity_main)

        setupButton(R.id.search, SearchActivity::class.java)
        setupButton(R.id.settings, SettingsActivity::class.java)
        setupButton(R.id.media_library, MediaLibraryActivity::class.java)
    }

    private fun <T> setupButton(buttonId: Int, targetActivity: Class<T>) {
        findViewById<MaterialButton>(buttonId).setOnClickListener {
            startActivity(Intent(this, targetActivity))
        }
    }
}
