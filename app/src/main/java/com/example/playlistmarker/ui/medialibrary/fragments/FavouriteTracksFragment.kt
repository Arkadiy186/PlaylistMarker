package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmarker.ui.medialibrary.handler.favouritetracks.UiStateFavouriteHandler
import com.example.playlistmarker.ui.medialibrary.handler.favouritetracks.UiStateFavouriteHandlerImpl
import com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks.FavouriteTracksUiState
import com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks.FragmentFavouriteTrackViewModel
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.example.playlistmarker.ui.search.recyclerview.TrackAdapter
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandler
import com.example.playlistmarker.ui.search.utills.sharing.NavigationContract
import com.example.playlistmarker.ui.search.utills.sharing.NavigationContractImpl
import kotlinx.coroutines.launch
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

    override fun onResume() {
        super.onResume()
        favouriteTrackViewModel.refreshFavourites()
    }

    private fun observeViewModels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favouriteTrackViewModel.favouriteTrackLivedata.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    private fun handleUiState(favouriteTracksUiState: FavouriteTracksUiState) {
        when(favouriteTracksUiState) {
            is FavouriteTracksUiState.Content -> {
                showTracks(favouriteTracksUiState.tracks)
            }
            is FavouriteTracksUiState.Placeholder -> {
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