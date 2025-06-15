package com.example.playlistmarker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmarker.data.db.entitys.TrackPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPlaylistDao {
    @Insert(entity = TrackPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTrack(track: TrackPlaylistEntity)

    @Query("SELECT * FROM track_playlist_table WHERE playlistId = :playlistId")
    fun getTracksForPlaylist(playlistId: Long): Flow<List<TrackPlaylistEntity>>
}