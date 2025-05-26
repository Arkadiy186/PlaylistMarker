package com.example.playlistmarker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmarker.data.db.dao.TrackDao
import com.example.playlistmarker.data.db.entitys.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}