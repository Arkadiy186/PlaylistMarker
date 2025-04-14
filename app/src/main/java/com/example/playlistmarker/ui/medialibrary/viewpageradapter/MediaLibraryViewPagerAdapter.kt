package com.example.playlistmarker.ui.medialibrary.viewpageradapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmarker.ui.medialibrary.fragments.FavouriteTracksFragment
import com.example.playlistmarker.ui.medialibrary.fragments.PlaylistFragment

class MediaLibraryViewPagerAdapter(
    fragment: Fragment,
    private val favouriteTrackViewPager: String,
    private val playlistTrackViewPager: String
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavouriteTracksFragment.newInstance(favouriteTrackViewPager)
            1 -> PlaylistFragment.newInstance(playlistTrackViewPager)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}