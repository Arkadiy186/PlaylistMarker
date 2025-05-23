package com.example.playlistmarker.ui.audioplayer.activity

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.AudioplayerBinding
import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.audioplayer.viewmodel.AudioPlayerViewModel
import com.example.playlistmarker.ui.main.activity.MainActivity
import com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks.FavouriteTracksUiState
import com.example.playlistmarker.ui.medialibrary.viewmodel.favouritetracks.FragmentFavouriteTrackViewModel
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: AudioplayerBinding
    private val audioPlayerViewModel: AudioPlayerViewModel by viewModel()
    private val favouriteTrackViewModel: FragmentFavouriteTrackViewModel by viewModel()

    private var lastPlayerState: UiAudioPlayerState? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audioplayer)
        binding = AudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()

        val trackInfoDetails = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", TrackInfoDetails::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<TrackInfoDetails>("track")
        }


        trackInfoDetails?.let {
            setupTrackInfo(it)
            audioPlayerViewModel.prepareTrack(it)
            loadGlideWithCorners(it)

            audioPlayerViewModel.savedPosition.observe(this) { savedPosition ->
                if (savedPosition > 0) {
                    audioPlayerViewModel.seekTo(savedPosition)
                    audioPlayerViewModel.playTrack()
                }
            }
        }

        audioPlayerViewModel.playerInfo.observe(this) { playerInfo ->
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

        audioPlayerViewModel.favouriteButtonState.observe(this) { state ->
            binding.likeTrackButton.setImageResource(state.resId)
        }
    }

    override fun onStop() {
        super.onStop()
        audioPlayerViewModel.savePosition()
        audioPlayerViewModel.stopTrack()
    }

    override fun onPause() {
        super.onPause()
        audioPlayerViewModel.savePosition()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setupListeners() {
        binding.audioPlayerToolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("navigate_to", "search")
            startActivity(intent)
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
        val cornerRadius = dpToPx(8f, this)
        Glide.with(this)
            .load(track.getCoverArtWork())
            .fitCenter()
            .placeholder(R.drawable.cover_album_placeholder)
            .transform(RoundedCorners(cornerRadius))
            .into(binding.albumCover)
    }
}