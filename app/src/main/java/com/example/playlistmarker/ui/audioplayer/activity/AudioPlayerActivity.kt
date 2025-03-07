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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.databinding.AudioplayerBinding
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerCallback
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.google.android.material.appbar.MaterialToolbar
import java.util.Date
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity(), AudioPlayerCallback {

    private lateinit var binding: AudioplayerBinding

    private val audioPlayerInteractor by lazy { Creator.provideAudioPlayerInteractor() }

    private var mainThreadHandler = Handler(Looper.getMainLooper())

    private val updateUiTimer = object : Runnable {
        override fun run() {
            if (audioPlayerInteractor.getPlayerState() == STATE_PLAYING) {
                updateTimeTrack()
                mainThreadHandler.removeCallbacks(this)
                mainThreadHandler.postDelayed(this, DELAY)
            }
        }
    }

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

        Log.d("AudioPlayerActivity", "Track Info received: $trackInfoDetails")

        trackInfoDetails?.let {
            setupTrackInfo(it)
            audioPlayerInteractor.preparePlayer(it)
            loadGlideWithCorners(it)
        } ?: run {
            Log.e("AudioPlayerActivity", "Track info is null")
        }

        audioPlayerInteractor.setCallback(this)
    }


    override fun onPause() {
        super.onPause()
        audioPlayerInteractor.pausePlayer()
        mainThreadHandler.removeCallbacks(updateUiTimer)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    override fun onPlayerPrepared() {
        runOnUiThread {
            binding.playTrackButton.isEnabled = true
            setPlayButton()
        }
    }

    override fun onPlayerCompleted() {
        runOnUiThread {
            setPlayButton()
            binding.currentTimeTrack.text = "00:00"
            mainThreadHandler.removeCallbacks(updateUiTimer)
            audioPlayerInteractor.pausePlayer()
        }
    }

    private fun setupListeners() {
        binding.audioPlayerToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.playTrackButton.setOnClickListener {
            Log.d("AudioPlayerActivity", "Play button clicked, current state: ${audioPlayerInteractor.getPlayerState()}")
            playbackControl()
        }
    }

    private fun setupTrackInfo(track: TrackInfoDetails) {
        Log.d("AudioPlayerActivity", "Setting up track info for: ${track.trackName}")

        binding.titleCover.text = track.trackName
        binding.authorTrack.text = track.artistName
        binding.trackTimeCurrentInfo.text = "00:30"
        binding.trackAlbumCurrentInfo.text = track.collectionName
        binding.trackReleaseDateCurrentInfo.text = formatReleaseDate(track.releaseDate)
        binding.trackGenreCurrentInfo.text = track.primaryGenreName
        binding.trackCountryCurrentInfo.text = track.country

        Log.d("AudioPlayerActivity", "Track setup complete: ${track.trackName}")
    }

    private fun playbackControl() {
        when(audioPlayerInteractor.getPlayerState()) {
            STATE_PLAYING -> {
                audioPlayerInteractor.pausePlayer()
                mainThreadHandler.removeCallbacks(updateUiTimer)
            }
            STATE_PAUSED, STATE_PREPARED -> {
                audioPlayerInteractor.startPlayer()
                mainThreadHandler.post(updateUiTimer)
            }
            else -> {
                audioPlayerInteractor.startPlayer()
                mainThreadHandler.post(updateUiTimer)
            }
        }
        setPlayButton()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private fun setPlayButton() {
        val playRes = if (audioPlayerInteractor.getPlayerState() == STATE_PLAYING) {
            R.drawable.ic_stop
        } else {
            R.drawable.ic_play
        }
        binding.playTrackButton.setImageResource(playRes)
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

    private fun updateTimeTrack() {
        val currentPosition = audioPlayerInteractor.getCurrentPosition()

        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(currentPosition.toLong()))

        runOnUiThread {
            binding.currentTimeTrack.text = formattedTime
        }
        Log.d("AudioPlayerActivity", "Current position: $currentPosition, formatted time: $formattedTime")
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

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 400L
    }
}