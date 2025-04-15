package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmarker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmarker.ui.medialibrary.viewmodel.FragmentFavouriteTrackViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavouriteTracksFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteTracksBinding
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
    }

    companion object {
        private const val TRACK_ID = "track_id"

        fun newInstance(trackId: String) = FavouriteTracksFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK_ID, trackId)
            }
        }
    }
}