package com.example.playlistmarker.ui.medialibrary.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.ActivityMediaLibraryBinding
import com.example.playlistmarker.ui.medialibrary.viewpageradapter.MediaLibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()

        val favouriteTrack = intent.getStringExtra("favourite") ?: ""
        val playlistTrack = intent.getStringExtra("playlist") ?: ""

        binding.viewPager.adapter = MediaLibraryViewPagerAdapter(
            this,
            favouriteTrackViewPager = favouriteTrack,
            playlistTrackViewPager = playlistTrack
        )

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favourite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

    private fun setupListeners() {
        binding.mediaLibraryToolbar.setNavigationOnClickListener {
            finish()
        }
    }
}