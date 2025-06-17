package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.FragmentCurrentPlaylistBinding
import com.example.playlistmarker.domain.db.model.Track
import com.example.playlistmarker.ui.medialibrary.viewmodel.currentplaylist.UiStateCurrentPlaylistTracks
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrentPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentCurrentPlaylistBinding

    private val playlistViewModel: PlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        observeViewModel()
    }

    private fun setupListeners() {
        binding.Toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        playlistViewModel.playlistDetailsState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiStateCurrentPlaylistTracks.Content -> {
                    showPlaylistCover(state.playlist.pathPictureCover)
                    showPlaylistTextInfo(state.playlist.name, state.playlist.description)
                    showTrackTimeSummary(state.tracks)
                }
                else -> {}
            }
        }
    }

    private fun showPlaylistCover(coverPath: String) {
        if (coverPath.isNotEmpty()) {
            Glide.with(requireContext())
                .load(coverPath)
                .into(binding.currentPlaylistImagePlaceholder)
        } else {
            binding.currentPlaylistImagePlaceholder.setImageResource(R.drawable.ic_playlist)
        }
    }

    private fun showPlaylistTextInfo(name: String, description: String) {
        binding.titleCurrentPlaylist.text = name
        binding.descriptionCurrentPlaylist.text = description
    }

    private fun showTrackTimeSummary(tracks: List<Track>) {
        val trackCount = tracks.size
        val trackCountText = resources.getQuantityString(
            R.plurals.tracks_count,
            trackCount,
            trackCount
        )
        val totalDurationMillis = tracks.sumOf { it.trackTime.toLongOrNull() ?: 0L }
        val totalMinutes = totalDurationMillis / 1000 / 60

        val fullTimeAndCountTracks = getString(R.string.playlist_time_and_count, totalMinutes, trackCountText)
        binding.timeAndCounterSongsCurrentPlaylist.text = fullTimeAndCountTracks
    }
}