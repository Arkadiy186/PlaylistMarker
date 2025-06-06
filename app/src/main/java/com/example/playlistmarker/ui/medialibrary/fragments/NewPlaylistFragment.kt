package com.example.playlistmarker.ui.medialibrary.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmarker.databinding.FragmentNewPlaylistBinding
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import java.io.File


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
        setupTextWatcher()
        setupPhotoPicker()
    }

    private fun setupListeners() {
        binding.Toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupTextWatcher() {
        binding.playlistNameEditText.addTextChangedListener { text ->
            val isNotBlank = !text.isNullOrBlank()
            binding.newPlaylistCreate.isEnabled = isNotBlank

            if (isNotBlank) {
                binding.newPlaylistCreate.setBackgroundResource(R.drawable.new_playlist_button_create_blue)
            } else {
                binding.newPlaylistCreate.setBackgroundResource(R.drawable.new_playlist_button_create_grey)
            }
        }
    }

    private fun setupPhotoPicker() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .into(binding.playlistImageView)

                binding.playlistImageIcon.visibility = View.GONE
            }
        }

        binding.playlistImageContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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