package com.example.playlistmarker.ui.medialibrary.viewpageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmarker.ui.medialibrary.fragments.FragmentFavouriteTracks
import com.example.playlistmarker.ui.medialibrary.fragments.FragmentPlaylists

class MediaLibraryViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val favouriteTrackViewPager: String,
    private val playlistTrackViewPager: String
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FragmentFavouriteTracks.newInstance(favouriteTrackViewPager)
            1 -> FragmentPlaylists.newInstance(playlistTrackViewPager)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}