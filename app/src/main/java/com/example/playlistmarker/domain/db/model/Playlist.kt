package com.example.playlistmarker.domain.db.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist (
    val id: Long,
    val name: String,
    val description: String,
    val pathPictureCover: String,
    val listIdTracks: List<String>,
    val counterTracks: Int
) : Parcelable