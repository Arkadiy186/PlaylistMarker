package com.example.playlistmarker.domain.search.model

data class Track (
    val id: Int,
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
)