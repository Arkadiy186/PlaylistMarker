package com.example.playlistmarker.data.db.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String,
    val pathPictureCover: String,
    val listIdTracks: List<String>,
    val counterTracks: Int
)
