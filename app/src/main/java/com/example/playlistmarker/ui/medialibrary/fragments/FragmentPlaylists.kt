package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmarker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmarker.ui.medialibrary.viewmodel.FragmentPlaylistViewModel
import org.koin.core.parameter.parametersOf

class FragmentPlaylists : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding
    private val playlistViewModel: FragmentPlaylistViewModel by viewModel {
        parametersOf(requireArguments().getString(PLAYLIST_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: String) = FragmentPlaylists().apply {
            arguments = Bundle().apply {
                putString(PLAYLIST_ID, playlistId)
            }
        }
    }
}