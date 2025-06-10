package com.example.playlistmarker.ui.audioplayer.fragment

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.databinding.AudioplayerBinding
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.audioplayer.handler.UiStateAudioPlayerPlaylistHandler
import com.example.playlistmarker.ui.audioplayer.handler.UiStateAudioPlayerPlaylistHandlerImpl
import com.example.playlistmarker.ui.audioplayer.recyclerview.AudioPlayerPlaylistAdapter
import com.example.playlistmarker.ui.audioplayer.viewmodel.AudioPlayerViewModel
import com.example.playlistmarker.ui.medialibrary.fragments.PlaylistFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmarker.ui.audioplayer.state.AddTrackToPlaylistState
import com.example.playlistmarker.ui.medialibrary.viewmodel.playlist.PlaylistUiState
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandler
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    private var _binding: AudioplayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlistAdapter: AudioPlayerPlaylistAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private lateinit var uiStateAudioPlayerPlaylistHandler: UiStateAudioPlayerPlaylistHandler

    private val audioPlayerViewModel: AudioPlayerViewModel by viewModel()
    private var lastPlayerState: UiAudioPlayerState? = null
    private val listPlaylists = mutableListOf<Playlist>()
    private val debounceHandler: DebounceHandler by lazy { Creator.provideDebounceHandler() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
        setupBottomSheet()

        val trackInfoDetails = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("track", TrackInfoDetails::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<TrackInfoDetails>("track")
        }

        trackInfoDetails?.let {
            setupTrackInfo(it)
            audioPlayerViewModel.prepareTrack(it)
            loadGlideWithCorners(it)

            audioPlayerViewModel.savedPosition.observe(viewLifecycleOwner) { savedPosition ->
                if (savedPosition > 0) {
                    audioPlayerViewModel.seekTo(savedPosition)
                    audioPlayerViewModel.playTrack()
                }
            }
        }

        onPlaylistClickDebounce = debounceHandler.debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) { playlist ->
            val track = audioPlayerViewModel.currentTrack.value

            if (track != null) {
                audioPlayerViewModel.addTrackToPlaylist(playlist, track)
            } else {
                Toast.makeText(requireContext(), "Трек не найден", Toast.LENGTH_SHORT).show()
            }
        }

        playlistAdapter = AudioPlayerPlaylistAdapter(listPlaylists) { playlist -> onPlaylistClickDebounce(playlist)}
        uiStateAudioPlayerPlaylistHandler = UiStateAudioPlayerPlaylistHandlerImpl(binding)

        binding.playlistAudioPlayerContent.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistAudioPlayerContent.adapter = playlistAdapter
    }

    override fun onResume() {
        super.onResume()

        val overlay = binding.overlay
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            overlay.visibility = View.VISIBLE
            overlay.alpha = 1f
        } else {
            overlay.visibility = View.GONE
            overlay.alpha = 0f
        }
    }

    override fun onStop() {
        super.onStop()
        audioPlayerViewModel.savePosition()
        audioPlayerViewModel.pauseTrack()
    }

    override fun onPause() {
        super.onPause()
        audioPlayerViewModel.savePosition()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun observeViewModel() {
        audioPlayerViewModel.playerInfo.observe(viewLifecycleOwner) { playerInfo ->
            val newPlayerState = playerInfo.playerState
            if (newPlayerState != lastPlayerState) {
                lastPlayerState = newPlayerState

                binding.playTrackButton.setImageResource(
                    when(playerInfo.playerState) {
                        is UiAudioPlayerState.Playing -> R.drawable.ic_stop
                        else -> R.drawable.ic_play
                    }
                )
            }
            binding.currentTimeTrack.text = playerInfo.currentTime
            binding.titleCover.text = playerInfo.currentTrack.trackName
            binding.authorTrack.text = playerInfo.currentTrack.artistName
            binding.trackTimeCurrentInfo.text = playerInfo.currentTrack.trackTime
            binding.trackAlbumCurrentInfo.text = playerInfo.currentTrack.collectionName
            binding.trackReleaseDateCurrentInfo.text = formatReleaseDate(playerInfo.currentTrack.releaseDate)
            binding.trackGenreCurrentInfo.text = playerInfo.currentTrack.primaryGenreName
            binding.trackCountryCurrentInfo.text = playerInfo.currentTrack.country
        }

        audioPlayerViewModel.favouriteButtonState.observe(viewLifecycleOwner) { state ->
            binding.likeTrackButton.setImageResource(state.resId)
        }

        audioPlayerViewModel.showPlaylistEvent.observe(viewLifecycleOwner) { _ ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        audioPlayerViewModel.observeAddPlaylistViewModel()
        audioPlayerViewModel.playlistUiState.observe(viewLifecycleOwner) { playlist ->
            handleUiState(playlist)
        }

        audioPlayerViewModel.addTrackState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is AddTrackToPlaylistState.TrackIsExists -> {
                    Toast.makeText(requireContext(), "Трек уже добавлен в \"${state.playlist}\"", Toast.LENGTH_SHORT).show()
                }
                is AddTrackToPlaylistState.TrackAdded -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    Toast.makeText(requireContext(), "Добавлено в плейлист \"${state.playlist}\"", Toast.LENGTH_SHORT).show()
                }
            }
        }

        audioPlayerViewModel.themeState.observe(viewLifecycleOwner) { isDark ->
            if (isDark) {
                binding.standardBottomSheet.setBackgroundResource(R.drawable.bottom_sheet_background_dark)
                binding.addToPlaylistTextTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                binding.standardBottomSheet.setBackgroundResource(R.drawable.bottom_sheet_background)
            }
        }
    }

    private fun setupListeners() {
        binding.audioPlayerToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.playTrackButton.setOnClickListener {
            val track = audioPlayerViewModel.currentTrack.value
            if (track != null) {
                when (audioPlayerViewModel.playerState.value) {
                    is UiAudioPlayerState.Playing -> audioPlayerViewModel.pauseTrack()
                    else -> audioPlayerViewModel.playTrack()
                }
            }
        }

        binding.likeTrackButton.setOnClickListener {
            audioPlayerViewModel.onFavoriteClicked()
        }

        binding.addTrackToPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            audioPlayerViewModel.addToPlaylistClicked()
        }

        binding.buttonNewPlaylist.setOnClickListener {
            val action = AudioPlayerFragmentDirections.actionAudioPlayerFragmentToNewPlaylistFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupTrackInfo(track: TrackInfoDetails) {

        binding.titleCover.text = track.trackName
        binding.authorTrack.text = track.artistName
        binding.trackTimeCurrentInfo.text = track.trackTime
        binding.trackAlbumCurrentInfo.text = track.collectionName
        binding.trackReleaseDateCurrentInfo.text = formatReleaseDate(track.releaseDate)
        binding.trackGenreCurrentInfo.text = track.primaryGenreName
        binding.trackCountryCurrentInfo.text = track.country
    }

    private fun setupBottomSheet() {
        val bottomSheetContainer = binding.standardBottomSheet
        val overlay = binding.overlay

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                        overlay.alpha = 0f
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        overlay.visibility = View.VISIBLE
                        overlay.alpha = 1f
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset.coerceIn(1f, 2f)
            }
        })
    }

    private fun handleUiState(playlistUiState: PlaylistUiState) {
        when(playlistUiState) {
            is PlaylistUiState.Content -> {
                showPlaylists(playlistUiState.playlists)
            }
            else -> {

            }
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        listPlaylists.clear()
        listPlaylists.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()
        uiStateAudioPlayerPlaylistHandler.setPlaceholderVisibility(true)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private fun formatReleaseDate(dateString: String): String {
        return try {
            val inputDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputDate = SimpleDateFormat("yyyy", Locale.getDefault())
            val date = inputDate.parse(dateString)
            outputDate.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun loadGlideWithCorners(track: TrackInfoDetails) {
        val cornerRadius = dpToPx(8f, requireContext())
        Glide.with(this)
            .load(track.getCoverArtWork())
            .fitCenter()
            .placeholder(R.drawable.cover_album_placeholder)
            .transform(RoundedCorners(cornerRadius))
            .into(binding.albumCover)
    }
}