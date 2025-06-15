package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmarker.databinding.FragmentCurrentPlaylistBinding
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
                }
                else -> {}
            }
        }
    }
}