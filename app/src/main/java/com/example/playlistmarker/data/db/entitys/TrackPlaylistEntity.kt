package com.example.playlistmarker.data.db.entitys

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "track_playlist_table",
    primaryKeys = ["trackId", "playlistId"],
    foreignKeys = [
        ForeignKey(entity = TrackEntity::class, parentColumns = ["trackId"], childColumns = ["trackId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = PlaylistEntity::class, parentColumns = ["playlistId"], childColumns = ["playlistId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("playlistId"), Index("trackId")]
)
data class TrackPlaylistEntity(
    val trackId: Long,
    val playlistId: Long,
)