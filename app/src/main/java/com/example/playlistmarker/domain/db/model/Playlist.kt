package com.example.playlistmarker.domain.db.model

data class Playlist (
    val id: Long,
    val name: String,
    val description: String,
    val pathPictureCover: String,
    val listIdTracks: List<String>,
    val counterTracks: Int
)