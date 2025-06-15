package com.example.playlistmarker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmarker.data.db.converters.PlaylistDbConverter
import com.example.playlistmarker.data.db.dao.FavouriteTrackDao
import com.example.playlistmarker.data.db.dao.PlaylistDao
import com.example.playlistmarker.data.db.dao.TrackPlaylistDao
import com.example.playlistmarker.data.db.entitys.FavouriteTrackEntity
import com.example.playlistmarker.data.db.entitys.PlaylistEntity
import com.example.playlistmarker.data.db.entitys.TrackPlaylistEntity

@Database(version = 2, entities = [FavouriteTrackEntity::class])
abstract class FavouriteTrackDatabase : RoomDatabase() {
    abstract fun favouriteTrackDao(): FavouriteTrackDao
}

@Database(version = 2, entities = [PlaylistEntity::class])
@TypeConverters(PlaylistDbConverter::class)
abstract class PlaylistDataBase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}

@Database(version = 2, entities = [TrackPlaylistEntity::class])
abstract class PlaylistTrackDatabase : RoomDatabase() {
    abstract fun playlistTrackDao(): TrackPlaylistDao
}