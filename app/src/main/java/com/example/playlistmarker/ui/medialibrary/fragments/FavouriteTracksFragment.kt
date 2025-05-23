package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmarker.ui.medialibrary.handler.UiStateFavouriteHandler
import com.example.playlistmarker.ui.medialibrary.handler.UiStateFavouriteHandlerImpl
import com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks.FavouriteTracksUiState
import com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks.FragmentFavouriteTrackViewModel
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.example.playlistmarker.ui.search.recyclerview.TrackAdapter
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandler
import com.example.playlistmarker.ui.search.utills.sharing.NavigationContract
import com.example.playlistmarker.ui.search.utills.sharing.NavigationContractImpl
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavouriteTracksFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteTracksBinding
    private lateinit var navigationContract: NavigationContract
    private lateinit var adapter: TrackAdapter
    private lateinit var uiStateFavouriteHandler: UiStateFavouriteHandler
    private lateinit var onTrackClickDebounce: (TrackInfoDetails) -> Unit

    private val debounceHandler: DebounceHandler by lazy { Creator.provideDebounceHandler() }
    private val favouriteList = mutableListOf<TrackInfoDetails>()
    private val favouriteTrackViewModel: FragmentFavouriteTrackViewModel by viewModel {
        parametersOf(requireArguments().getString(TRACK_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationContract = NavigationContractImpl(requireContext())

        onTrackClickDebounce = debounceHandler.debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) { track ->
            navigationContract.openAudioPlayer(track)
        }

        adapter = TrackAdapter(favouriteList) { track -> onTrackClickDebounce(track) }
        uiStateFavouriteHandler = UiStateFavouriteHandlerImpl(binding, this)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        observeViewModels()
    }

    private fun observeViewModels() {
        favouriteTrackViewModel.favouriteTrackLivedata.observe(viewLifecycleOwner) { state ->
            handleUiState(state)
        }
    }

    private fun handleUiState(favouriteTracksUiState: FavouriteTracksUiState) {
        Log.d("FavouriteTracksFragment", "UI state: $favouriteTracksUiState")
        when(favouriteTracksUiState) {
            is FavouriteTracksUiState.Content -> {
                Log.d("FavouriteTracksFragment", "Got ${favouriteTracksUiState.tracks.size} tracks")
                showTracks(favouriteTracksUiState.tracks)
            }
            else -> {
                Log.d("FavouriteTracksFragment", "No favourite tracks found")
                uiStateFavouriteHandler.favouriteNotFound()
            }
        }
    }

    private fun showTracks(tracks: List<TrackInfoDetails>) {
        requireActivity().runOnUiThread {
            favouriteList.clear()
            favouriteList.addAll(tracks)
            adapter.notifyDataSetChanged()
            uiStateFavouriteHandler.placeholderSetVisibility(true)
        }
    }

    companion object {
        private const val TRACK_ID = "track_id"
        const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance(trackId: String) = FavouriteTracksFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK_ID, trackId)
            }
        }
    }
}