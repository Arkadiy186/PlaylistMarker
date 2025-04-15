package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.FragmentMediaLibraryBinding
import com.example.playlistmarker.ui.medialibrary.viewpageradapter.MediaLibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryFragment : Fragment() {
    private lateinit var binding: FragmentMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favouriteTrack = arguments?.getString(ARGS_TRACK_FAVOURITE) ?: ""
        val playlistTrack = arguments?.getString(ARGS_TRACK_PLAYLIST) ?: ""

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

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }

    companion object {
        private const val ARGS_TRACK_FAVOURITE = "track_id"
        private const val ARGS_TRACK_PLAYLIST = "playlist_id"

        const val TAG = "MediaLibraryFragment"

        fun newInstance(trackId: String, playlistId: String): Fragment {
            return MediaLibraryFragment().apply {
                arguments = bundleOf(
                    ARGS_TRACK_FAVOURITE to trackId,
                    ARGS_TRACK_PLAYLIST to playlistId
                )
            }
        }
    }
}