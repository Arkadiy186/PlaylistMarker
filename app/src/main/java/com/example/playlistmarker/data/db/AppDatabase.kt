package com.example.playlistmarker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmarker.data.db.converters.PlaylistDbConverter
import com.example.playlistmarker.data.db.dao.FavouriteTrackDao
import com.example.playlistmarker.data.db.dao.PlaylistDao
import com.example.playlistmarker.data.db.dao.TrackDao
import com.example.playlistmarker.data.db.dao.TrackPlaylistDao
import com.example.playlistmarker.data.db.entitys.FavouriteTrackEntity
import com.example.playlistmarker.data.db.entitys.PlaylistEntity
import com.example.playlistmarker.data.db.entitys.TrackEntity
import com.example.playlistmarker.data.db.entitys.TrackPlaylistEntity

@Database(
    version = 1,
    entities = [
        FavouriteTrackEntity::class,
        PlaylistEntity::class,
        TrackEntity::class,
        TrackPlaylistEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteTrackDao(): FavouriteTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
    abstract fun trackPlaylistDao(): TrackPlaylistDao
}