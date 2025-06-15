package com.example.playlistmarker.ui.search.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackInfoDetails(
    val id: Int,
    val playlistId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavourite: Boolean = false,
    var addedAt: Long = 0L
) : Parcelable {

    fun getCoverArtWork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

}
