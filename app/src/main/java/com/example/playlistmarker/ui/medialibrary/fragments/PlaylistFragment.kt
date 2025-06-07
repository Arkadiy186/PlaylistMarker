package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.FragmentPlaylistViewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

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

        setupListeners()

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("playlist_created")?.observe(viewLifecycleOwner) { playlistName ->
            Toast.makeText(requireContext(), "Плейлист $playlistName создан", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }
    }

    companion object {
        private const val PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: String) = PlaylistFragment().apply {
            arguments = Bundle().apply {
                putString(PLAYLIST_ID, playlistId)
            }
        }
    }
}