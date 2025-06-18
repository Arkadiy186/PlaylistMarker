package com.example.playlistmarker.ui.medialibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.ui.medialibrary.viewmodel.editplaylist.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : NewPlaylistFragment() {
    private val editPlaylistViewModel: EditPlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlist = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("playlist", Playlist::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Playlist>("playlist")
        }
        if (playlist != null) {
            editPlaylistViewModel.loadPlaylist(playlist)
        }

        observeViewModel()


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun setupListeners() {
        binding.Toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.newPlaylistCreate.setOnClickListener {
            val name = binding.playlistNameEditText.text.toString()
            val description = binding.playlistDescriptionEditText.text.toString()
            val pathPictureCover = selectedImageUri?.path ?: ""

            val current = editPlaylistViewModel.currentPlaylist.value
            if (current != null) {
                val updatePlaylist = current.copy(
                    name = name,
                    description = description,
                    pathPictureCover = pathPictureCover
                )

                editPlaylistViewModel.updatePlaylist(updatePlaylist)
                findNavController().popBackStack()
            }
        }
    }

    private fun observeViewModel() {
        editPlaylistViewModel.currentPlaylist.observe(viewLifecycleOwner) { playlist ->
            binding.Toolbar.setTitle(R.string.edit_playlist_title)
            binding.playlistNameEditText.setText(playlist?.name)
            binding.playlistDescriptionEditText.setText(playlist?.description)

            if (!playlist?.pathPictureCover.isNullOrEmpty()) {
                val cornerRadius = dpToPx(8f, requireContext())
                Glide.with(requireContext())
                    .load(playlist?.pathPictureCover)
                    .transform(CenterCrop(), RoundedCorners(cornerRadius))
                    .into(binding.playlistImageView)

                binding.playlistImageIcon.visibility = View.GONE
            }
            binding.newPlaylistCreate.setText(R.string.save_edit_playlist)
        }
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: Long): EditPlaylistFragment {
            val fragment = EditPlaylistFragment()
            val args = Bundle()
            args.putLong(ARG_PLAYLIST_ID, playlistId)
            fragment.arguments = args
            return fragment
        }
    }
}