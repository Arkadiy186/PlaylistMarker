package com.example.playlistmarker

import android.content.Context
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.trackrecyclerview.Track
import com.google.android.material.appbar.MaterialToolbar
import java.util.Date
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var backButton : MaterialToolbar
    private lateinit var coverAlbum : ImageView
    private lateinit var nameTrack : TextView
    private lateinit var authorTrackTextView : TextView
    private lateinit var trackTimeTextView : TextView
    private lateinit var collectionNameTextView : TextView
    private lateinit var releaseDateTextView : TextView
    private lateinit var primaryGenreNameTextView : TextView
    private lateinit var countryTextView : TextView
    private lateinit var playButton : ImageView
    private lateinit var timeTrack : TextView

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var url : String = ""
    private var mainThreadHandler : Handler? = null

    private val updateUiTimer = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(mediaPlayer.currentPosition.toLong()))

                runOnUiThread {
                    timeTrack.text = formattedTime
                }

                mainThreadHandler?.removeCallbacks(this)
                mainThreadHandler?.postDelayed(this, DELAY)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audioplayer)

        backButton = findViewById(R.id.audioPlayerToolbar)
        coverAlbum = findViewById(R.id.albumCover)
        nameTrack = findViewById(R.id.titleCover)
        authorTrackTextView = findViewById(R.id.authorTrack)
        trackTimeTextView = findViewById(R.id.trackTimeCurrentInfo)
        collectionNameTextView = findViewById(R.id.trackAlbumCurrentInfo)
        releaseDateTextView = findViewById(R.id.trackReleaseDateCurrentInfo)
        primaryGenreNameTextView = findViewById(R.id.trackGenreCurrentInfo)
        countryTextView = findViewById(R.id.trackCountryCurrentInfo)
        playButton = findViewById(R.id.playTrackButton)
        timeTrack = findViewById(R.id.currentTimeTrack)

        mainThreadHandler = Handler(Looper.getMainLooper())

        backButton.setNavigationOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }

        val track = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Track>("track")
        }

        if (track != null) {
            nameTrack.text = track.trackName
            authorTrackTextView.text = track.artistName
            trackTimeTextView.text = "00:30"
            collectionNameTextView.text = track.collectionName
            releaseDateTextView.text = formatReleaseDate(track.releaseDate)
            primaryGenreNameTextView.text = track.primaryGenreName
            countryTextView.text = track.country
            url = track.previewUrl

            preparePlayer()

            val cornerRadius = dpToPx(8f, this)
            Glide.with(this)
                .load(track.getCoverArtWork())
                .fitCenter()
                .placeholder(R.drawable.cover_album_placeholder)
                .transform(RoundedCorners(cornerRadius))
                .into(coverAlbum)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        mainThreadHandler?.removeCallbacks(updateUiTimer)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
            setPlayButton()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            setPlayButton()
            mainThreadHandler?.removeCallbacks(updateUiTimer)
            timeTrack.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        setPlayButton()
        mainThreadHandler?.post(updateUiTimer)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        setPlayButton()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private fun setPlayButton() {
        val playRes = if (playerState == STATE_PLAYING) {
            R.drawable.ic_stop
        } else {
            R.drawable.ic_play
        }
        playButton.setImageResource(playRes)
    }

    private fun formatReleaseDate(dateString: String) : String {
        return try {
            val inputDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputDate = SimpleDateFormat("yyyy", Locale.getDefault())
            val date = inputDate.parse(dateString)
            outputDate.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 400L
    }
}