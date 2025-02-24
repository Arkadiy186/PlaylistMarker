package com.example.playlistmarker.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackDto (@SerializedName("trackName") val trackName: String,
                     @SerializedName("artistName") val artistName: String,
                     @SerializedName("trackTimeMillis") val trackTime: Long,
                     @SerializedName("artworkUrl100") val artworkUrl100: String,
                     @SerializedName("collectionName") val collectionName: String,
                     @SerializedName("releaseDate") val releaseDate: String,
                     @SerializedName("primaryGenreName") val primaryGenreName: String,
                     @SerializedName("country") val country: String,
                     @SerializedName("previewUrl") val previewUrl: String
) : Parcelable {
                      constructor(parcel: Parcel) : this (
                          parcel.readString() ?: "",
                          parcel.readString() ?: "",
                          parcel.readLong(),
                          parcel.readString() ?: "",
                          parcel.readString() ?: "",
                          parcel.readString() ?: "",
                          parcel.readString() ?: "",
                          parcel.readString() ?: "",
                          parcel.readString() ?: ""
                      )

    override fun describeContents(): Int = 0

    companion object : Parceler<TrackDto> {
        override fun TrackDto.write(parcel: Parcel, flags: Int) {
            parcel.writeString(trackName)
            parcel.writeString(artistName)
            parcel.writeLong(trackTime)
            parcel.writeString(artworkUrl100)
            parcel.writeString(collectionName)
            parcel.writeString(releaseDate)
            parcel.writeString(primaryGenreName)
            parcel.writeString(country)
            parcel.writeString(previewUrl)
        }

        override fun create(parcel: Parcel): TrackDto = TrackDto(parcel)
    }
}

