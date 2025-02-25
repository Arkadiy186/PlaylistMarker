package com.example.playlistmarker.presentation.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
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
    constructor(parcel: Parcel) : this (
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int = 0

    fun getCoverArtWork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    companion object : Parceler<TrackInfoDetails> {

        override fun TrackInfoDetails.write(parcel: Parcel, flags: Int) {
            parcel.writeString(trackName)
            parcel.writeString(artistName)
            parcel.writeString(trackTime)
            parcel.writeString(artworkUrl100)
            parcel.writeString(collectionName)
            parcel.writeString(releaseDate)
            parcel.writeString(primaryGenreName)
            parcel.writeString(country)
            parcel.writeString(previewUrl)
        }

        override fun create(parcel: Parcel): TrackInfoDetails {
            return TrackInfoDetails(parcel)
        }
    }
}
