package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.databinding.FragmentPlaylistsBinding
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.ui.medialibrary.handler.playlists.UiStatePlaylistHandler
import com.example.playlistmarker.ui.medialibrary.handler.playlists.UiStatePlaylistsHandlerImpl
import com.example.playlistmarker.ui.medialibrary.recyclerview.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.PlaylistUiState
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.PlaylistViewModel
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandler
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var binding: FragmentPlaylistsBinding
    private lateinit var uiStatePlaylistHandler: UiStatePlaylistHandler
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private val debounceHandler: DebounceHandler by lazy { Creator.provideDebounceHandler() }
    private val playlistViewModel: PlaylistViewModel by viewModel()
    private val listPlaylists = mutableListOf<Playlist>()

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

        onPlaylistClickDebounce = debounceHandler.debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) { playlist ->

        }

        playlistAdapter = PlaylistAdapter(listPlaylists) { playlist -> onPlaylistClickDebounce(playlist)}
        uiStatePlaylistHandler = UiStatePlaylistsHandlerImpl(binding, this)

        binding.playlistContent.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistContent.adapter = playlistAdapter

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setupListeners() {
        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }
    }

    private fun observeViewModel() {
        playlistViewModel.playlistsState.observe(viewLifecycleOwner) { playlist ->
            handleUiState(playlist)
        }
    }

    private fun handleUiState(playlistUiState: PlaylistUiState) {
        when(playlistUiState) {
            is PlaylistUiState.Content -> {
                showPlaylists(playlistUiState.playlists)
            }
            is PlaylistUiState.Placeholder -> {
                uiStatePlaylistHandler.setPlaceholderVisibility(false)
            }
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        requireActivity().runOnUiThread {
            listPlaylists.clear()
            listPlaylists.addAll(playlists)
            playlistAdapter.notifyDataSetChanged()
            uiStatePlaylistHandler.setPlaceholderVisibility(true)
        }
    }

    companion object {
        private const val PLAYLIST_ID = "playlist_id"
        const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance(playlistId: String) = PlaylistFragment().apply {
            arguments = Bundle().apply {
                putString(PLAYLIST_ID, playlistId)
            }
        }
    }
}