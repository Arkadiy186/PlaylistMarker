package com.example.playlistmarker.ui.audioplayer.activity

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.databinding.AudioplayerBinding
import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.audioplayer.impl.AudioPlayerInteractorImpl
import com.example.playlistmarker.ui.audioplayer.viewmodel.AudioPlayerViewModel
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.google.android.material.appbar.MaterialToolbar
import java.util.Date
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: AudioplayerBinding
    private lateinit var audioPlayerViewModel: AudioPlayerViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audioplayer)
        binding = AudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioPlayerViewModel = ViewModelProvider(this)[AudioPlayerViewModel::class.java]

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

            val savedPosition = audioPlayerViewModel.loadSavePosition()
            if (savedPosition > 0) {
                audioPlayerViewModel.seekTo(savedPosition)
                audioPlayerViewModel.playTrack()
            }

        } ?: run {}

        audioPlayerViewModel.playerState.observe(this) { state ->
            Log.d("MediaPlayer", "state: ${state}")
            when(state) {
                UiAudioPlayerState.STATE_PLAYING -> binding.playTrackButton.setImageResource(R.drawable.ic_stop)
                UiAudioPlayerState.STATE_STOPPED,
                UiAudioPlayerState.STATE_COMPLETED -> {
                    Log.d("MediaPlayer", "image: ${R.drawable.ic_play}")
                    binding.playTrackButton.setImageResource(R.drawable.ic_play)
                    audioPlayerViewModel.resetTrackTime()
                }
                UiAudioPlayerState.STATE_PREPARED,
                UiAudioPlayerState.STATE_PAUSED,
                UiAudioPlayerState.STATE_DEFAULT -> binding.playTrackButton.setImageResource(R.drawable.ic_play)
            }
        }

        audioPlayerViewModel.currentTime.observe(this) { time ->
            binding.currentTimeTrack.text = time
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
        Log.d("MediaPlayer", "onDestroy")
        audioPlayerViewModel.resetTrackTime()
    }

    private fun setupListeners() {
        binding.audioPlayerToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.playTrackButton.setOnClickListener {
            val track = audioPlayerViewModel.currentTrack.value
            if (track != null) {
                when (audioPlayerViewModel.playerState.value) {
                    UiAudioPlayerState.STATE_PLAYING -> audioPlayerViewModel.pauseTrack()
                    else -> audioPlayerViewModel.playTrack()
                }
            }
        }
    }

    private fun setupTrackInfo(track: TrackInfoDetails) {
        Log.d("AudioPlayerActivity", "Setting up track info for: ${track.trackName}")

        binding.titleCover.text = track.trackName
        binding.authorTrack.text = track.artistName
        binding.trackTimeCurrentInfo.text = track.trackTime
        binding.trackAlbumCurrentInfo.text = track.collectionName
        binding.trackReleaseDateCurrentInfo.text = formatReleaseDate(track.releaseDate)
        binding.trackGenreCurrentInfo.text = track.primaryGenreName
        binding.trackCountryCurrentInfo.text = track.country

        Log.d("AudioPlayerActivity", "Track setup complete: ${track.trackName}")
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