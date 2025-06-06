package com.example.playlistmarker.ui.medialibrary.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmarker.databinding.FragmentPlaylistsBinding
import com.example.playlistmarker.ui.main.activity.MainActivity

class NewPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentNewPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        binding.Toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
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