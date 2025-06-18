package com.example.playlistmarker.ui.medialibrary.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.window.OnBackInvokedCallback
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmarker.databinding.FragmentNewPlaylistBinding
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.PlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


open class NewPlaylistFragment : Fragment() {

    private val playlistViewModel: PlaylistViewModel by viewModel()

    internal lateinit var binding: FragmentNewPlaylistBinding
    internal var isPlaylistNameNotBlank: Boolean = false
    internal var selectedImageUri: Uri? = null

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

        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val currentFocusView = requireActivity().currentFocus
                currentFocusView?.clearFocus()
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                v.performClick()
            }
            false
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackAction()
        }

        binding.playlistNameLayout.cursorColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))
        binding.playlistDescriptionLayout.cursorColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))
    }

    private fun observeViewModel() {
        playlistViewModel.playlistsState.observe(viewLifecycleOwner) { playlist ->

        }
    }

    protected open fun setupListeners() {
        binding.Toolbar.setNavigationOnClickListener {
            handleBackAction()
        }

        binding.newPlaylistCreate.setOnClickListener {
            val name = binding.playlistNameEditText.text.toString()
            val description = binding.playlistDescriptionEditText.text.toString()
            val pathPictureCover = selectedImageUri?.path ?: ""
            val listIdTracks = mutableListOf<String>()
            val counterTracks = listIdTracks.size
            val playlist = Playlist(
                id = 0L,
                name = name,
                description = description,
                pathPictureCover = pathPictureCover,
                listIdTracks = listIdTracks,
                counterTracks = counterTracks
            )

            playlistViewModel.createPlaylist(playlist)

            findNavController().previousBackStackEntry?.savedStateHandle?.set("playlist_created", playlist.name)
            findNavController().popBackStack()
        }
    }

    private fun setupTextWatcher() {
        binding.playlistNameEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHintColor(hasFocus, binding.playlistNameEditText.text)
        }

        binding.playlistDescriptionEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHintColor(hasFocus, binding.playlistNameEditText.text)
        }

        binding.playlistNameEditText.addTextChangedListener { text ->
            val hasFocus = binding.playlistNameEditText.hasFocus()
            val isNotBlank = !text.isNullOrBlank()
            isPlaylistNameNotBlank = isNotBlank
            binding.newPlaylistCreate.isEnabled = isNotBlank

            val blueColor = ContextCompat.getColor(requireContext(), R.color.blue)
            val greyColor = ContextCompat.getColor(requireContext(), R.color.grey)

            val color = when {
                hasFocus -> blueColor
                isNotBlank -> blueColor
                else -> greyColor
            }

            val hintColor = ColorStateList.valueOf(color)
            binding.playlistNameLayout.hintTextColor = hintColor
            binding.playlistNameLayout.boxStrokeColor = color

            binding.newPlaylistCreate.backgroundTintList = ColorStateList.valueOf(color)
        }


    }

    private fun updateHintColor(hasFocus: Boolean, text: Editable?) {
        playlistViewModel.themeState.observe(viewLifecycleOwner) { isDark ->
            val context = requireContext()
            val color = when {
                hasFocus || !text.isNullOrBlank() -> ContextCompat.getColor(context, R.color.blue)
                isDark -> ContextCompat.getColor(context, R.color.white)
                else -> ContextCompat.getColor(context, R.color.black_for_text)
            }

            val hintColor = ColorStateList.valueOf(color)

            binding.playlistNameLayout.hintTextColor = hintColor
            binding.playlistDescriptionLayout.hintTextColor = hintColor
            binding.playlistNameLayout.boxStrokeColor = color
            binding.playlistDescriptionLayout.boxStrokeColor = color
            binding.playlistNameEditText.setTextCursorDrawable(R.drawable.cursor)
        }
    }


    private fun setupPhotoPicker() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val savedImagePath = saveImageToPrivateStorage(uri)
                if (savedImagePath != null) {
                    selectedImageUri = Uri.fromFile(File(savedImagePath))
                }
                val cornerRadius = dpToPx(8f, requireContext())
                Glide.with(this)
                    .load(selectedImageUri)
                    .transform(CenterCrop(), RoundedCorners(cornerRadius))
                    .into(binding.playlistImageView)

                binding.playlistImageIcon.visibility = View.GONE
            }
        }

        binding.playlistImageContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    internal fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private fun saveImageToPrivateStorage(uri: Uri): String? {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val uniqueFileName = "cover_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, uniqueFileName)

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
            .setTitle(R.string.final_playlist_creation)
            .setMessage(R.string.unsaved_data_be_lost)
            .setNeutralButton(R.string.cancel_create_playlist) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.final_create_playlist) { dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .show()
    }

    private fun handleBackAction() {
        if (isPlaylistNameNotBlank || selectedImageUri != null) {
            dialogToConfirmCreateNewPlaylist()
        } else {
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