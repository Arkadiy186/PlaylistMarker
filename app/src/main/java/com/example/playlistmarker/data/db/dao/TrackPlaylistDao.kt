package com.example.playlistmarker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmarker.data.db.entitys.TrackPlaylistEntity

@Dao
interface TrackPlaylistDao {
    @Insert(entity = TrackPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTrack(track: TrackPlaylistEntity)
}