package com.example.playlistmarker

import android.content.Context
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.trackrecyclerview.Track
import com.google.android.material.appbar.MaterialToolbar
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

        backButton.setNavigationOnClickListener {
            finish()
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
            trackTimeTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
            collectionNameTextView.text = track.collectionName
            releaseDateTextView.text = formatReleaseDate(track.releaseDate)
            primaryGenreNameTextView.text = track.primaryGenreName
            countryTextView.text = track.country
        }

        val cornerRadius = dpToPx(8f, this)

        if (track != null) {
            Glide.with(this)
                .load(track.getCoverArtWork())
                .fitCenter()
                .placeholder(R.drawable.cover_album_placeholder)
                .transform(RoundedCorners(cornerRadius))
                .into(coverAlbum)
        }

        if (isDark()) {
            playButton.setImageResource(R.drawable.play_dark)
        } else {
            playButton.setImageResource(R.drawable.play_light)
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private fun isDark(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
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
}