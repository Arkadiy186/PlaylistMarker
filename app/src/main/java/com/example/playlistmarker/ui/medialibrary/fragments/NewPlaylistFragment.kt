package com.example.playlistmarker.ui.medialibrary.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmarker.databinding.FragmentNewPlaylistBinding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.FragmentNewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


class NewPlaylistFragment : Fragment() {

    private val viewModel: FragmentNewPlaylistViewModel by viewModel()

    private lateinit var binding: FragmentNewPlaylistBinding
    private var isPlaylistNameNotBlank: Boolean = false
    private var selectedImageUri: Uri? = null

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
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlist ->

        }
    }

    private fun setupListeners() {
        binding.Toolbar.setNavigationOnClickListener {
            if (isPlaylistNameNotBlank || selectedImageUri != null) {
                dialogToConfirmCreateNewPlaylist()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.newPlaylistCreate.setOnClickListener {
            val name = binding.playlistNameEditText.text.toString()
            val description = binding.playlistDescriptionEditText.text.toString()
            val pathPictureCover = selectedImageUri?.path ?: ""
            val listIdTracks = "[]"
            val counterTracks = 0
            val playlist = Playlist(
                id = 0L,
                name = name,
                description = description,
                pathPictureCover = pathPictureCover,
                listIdTracks = listIdTracks,
                counterTracks = counterTracks
            )

            viewModel.createPlaylist(playlist)

            findNavController().previousBackStackEntry?.savedStateHandle?.set("playlist_created", playlist.name)
            findNavController().popBackStack()
        }
    }

    private fun setupTextWatcher() {
        binding.playlistNameEditText.addTextChangedListener { text ->
            val isNotBlank = !text.isNullOrBlank()
            isPlaylistNameNotBlank = isNotBlank
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
                val savedImagePath = saveImageToPrivateStorage(uri)
                if (savedImagePath != null) {
                    selectedImageUri = Uri.fromFile(File(savedImagePath))
                }
                Glide.with(this)
                    .load(selectedImageUri)
                    .into(binding.playlistImageView)
                saveImageToPrivateStorage(uri)

                binding.playlistImageIcon.visibility = View.GONE
            }
        }

        binding.playlistImageContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String? {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "first_cover.jpg")

        try {
            requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    FileOutputStream(file).use { outputStream ->
                        val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                        if (success) {
                            return file.absolutePath
                        }
                    }
                }
            }
        } catch (_: Exception) {

        }
        return null
    }

    private fun dialogToConfirmCreateNewPlaylist() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Завершить") { dialog, which ->
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .show()
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