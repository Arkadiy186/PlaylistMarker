package com.example.playlistmarker.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackInfoDetails(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Parcelable {

    fun getCoverArtWork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

}
