package com.example.playlistmarker.ui.medialibrary.fragments

import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.databinding.FragmentCurrentPlaylistBinding
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.ui.medialibrary.utils.UtilsCurrentPlaylist
import com.example.playlistmarker.ui.medialibrary.viewmodel.currentplaylist.UiStateCurrentPlaylistTracks
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.PlaylistViewModel
import com.example.playlistmarker.ui.search.fragment.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.example.playlistmarker.ui.search.recyclerview.TrackAdapter
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandler
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CurrentPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentCurrentPlaylistBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: TrackAdapter
    private lateinit var onTrackClickDebounce: (TrackInfoDetails) -> Unit
    private lateinit var utilsCurrentPlaylist: UtilsCurrentPlaylist
    private lateinit var currentPlaylist: Playlist

    private val playlistViewModel: PlaylistViewModel by viewModel()
    private val listTracks = mutableListOf<TrackInfoDetails>()
    private val debounceHandler: DebounceHandler by lazy { Creator.provideDebounceHandler() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utilsCurrentPlaylist = UtilsCurrentPlaylist()

        val playlistId = arguments?.getLong("id") ?: return
        playlistViewModel.loadPlaylistWithTracks(playlistId)

        onTrackClickDebounce = debounceHandler.debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) { track ->
            val action = CurrentPlaylistFragmentDirections.actionCurrentPlaylistFragment2ToAudioPlayerFragment(track)
            findNavController().navigate(action)
        }

        setupListeners()
        setupBottomSheet()

        adapter = TrackAdapter(listTracks,
            onItemClickListener = { track ->
                onTrackClickDebounce(track)
            },
            onItemLongClickListener = { track ->
                dialogToRemoveTrack(track.id, currentPlaylist)
            })
        binding.currentPlaylistSongs.layoutManager = LinearLayoutManager(requireContext())
        binding.currentPlaylistSongs.adapter = adapter

        observeViewModel()
    }

    private fun setupListeners() {
        binding.Toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        playlistViewModel.playlistDetailsState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiStateCurrentPlaylistTracks.Content -> {
                    currentPlaylist = state.playlist
                    showPlaylistCover(state.playlist.pathPictureCover)
                    showPlaylistTextInfo(state.playlist.name, state.playlist.description)
                    showTrackTimeSummary(state.tracks)
                    showTracks(state.tracks)
                }
                else -> {}
            }
        }
    }

    private fun setupBottomSheet() {
        val bottomSheetContainer = binding.bottomSheetContainerCurrentPlaylist
        val overlay = binding.overlay

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        overlay.alpha = 1f
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        })
    }

    private fun showPlaylistCover(coverPath: String) {
        if (coverPath.isNotEmpty()) {
            Glide.with(requireContext())
                .load(coverPath)
                .into(binding.currentPlaylistImagePlaceholder)
        } else {
            binding.currentPlaylistImagePlaceholder.setImageResource(R.drawable.ic_playlist)
        }
    }

    private fun showPlaylistTextInfo(name: String, description: String) {
        binding.titleCurrentPlaylist.text = name
        binding.descriptionCurrentPlaylist.text = description
    }

    private fun showTrackTimeSummary(tracks: List<TrackInfoDetails>) {
        val trackCount = tracks.size
        val trackCountText = resources.getQuantityString(
            R.plurals.tracks_count,
            trackCount,
            trackCount
        )
        val totalDurationMillis = tracks.sumOf { utilsCurrentPlaylist.parseTrackTimeMillis(it.trackTime) }

        val formatter = SimpleDateFormat("mm", Locale.getDefault()).format(totalDurationMillis)

        val fullTimeAndCountTracks = getString(R.string.playlist_time_and_count, formatter, trackCountText)
        binding.timeAndCounterSongsCurrentPlaylist.text = fullTimeAndCountTracks
    }

    private fun showTracks(track: List<TrackInfoDetails>) {
        listTracks.clear()
        listTracks.addAll(track)
        adapter.notifyDataSetChanged()
     }

    private fun dialogToRemoveTrack(trackId: Long, playlist: Playlist) {
        val title = TextView(requireContext()).apply {
            text = getText(R.string.dialog_title_remove_track_from_playlist)
            setTextColor(ContextCompat.getColor(context, R.color.black_for_text))
            textSize = 14f
            letterSpacing = 0.05f
            setPadding(
                utilsCurrentPlaylist.dpToPx(24, requireContext()),
                utilsCurrentPlaylist.dpToPx(23, requireContext()),
                utilsCurrentPlaylist.dpToPx(8, requireContext()),
                utilsCurrentPlaylist.dpToPx(36, requireContext()))
            typeface = ResourcesCompat.getFont(context, R.font.ys_display_regular)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setCustomTitle(title)
            .setNegativeButton(R.string.dialog_failure_remove_track_from_playlist) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.dialog_confirm_remove_track_from_playlist) { dialog, _ ->
                playlistViewModel.deleteTrackFromPlaylist(trackId, playlist)
                dialog.dismiss()
            }
            .show()

        val marginHorizontal = utilsCurrentPlaylist.dpToPx(40, requireContext())
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val dialogWidth = screenWidth - marginHorizontal * 2

        dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background_rounded)
        )

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.blue)
        )

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.blue)
        )
    }
}