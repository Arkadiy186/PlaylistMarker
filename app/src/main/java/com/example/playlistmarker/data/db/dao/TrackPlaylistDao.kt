package com.example.playlistmarker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmarker.data.db.entitys.TrackEntity
import com.example.playlistmarker.data.db.entitys.TrackPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPlaylistDao {
    @Insert(entity = TrackPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTrack(track: TrackPlaylistEntity)

    @Query("DELETE FROM track_playlist_table WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long)

    @Query("DELETE FROM track_playlist_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Long)

    @Query("""
    SELECT t.* FROM track_table t
    INNER JOIN track_playlist_table tp ON t.trackId = tp.trackId
    WHERE tp.playlistId = :playlistId
""")
    fun getTracksForPlaylist(playlistId: Long): Flow<List<TrackEntity>>
}