package com.example.playlistmarker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmarker.data.db.dao.PlaylistDao
import com.example.playlistmarker.data.db.dao.TrackDao
import com.example.playlistmarker.data.db.entitys.PlaylistEntity
import com.example.playlistmarker.data.db.entitys.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class TrackDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}

@Database(version = 2, entities = [PlaylistEntity::class])
abstract class PlaylistDataBase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}