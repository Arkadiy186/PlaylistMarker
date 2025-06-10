package com.example.playlistmarker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmarker.data.db.entitys.FavouriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteTrackDao {

    @Insert(entity = FavouriteTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavouriteTrackEntity)

    @Delete(entity = FavouriteTrackEntity::class)
    suspend fun deleteTrack(track: FavouriteTrackEntity)

    @Query("SELECT * FROM favourite_track_table")
    fun getTracks(): Flow<List<FavouriteTrackEntity>>

    @Query("SELECT id FROM favourite_track_table")
    fun getIdTracks(): Flow<List<Int>>

    @Query("SELECT * FROM favourite_track_table WHERE id = :trackId LIMIT 1")
    suspend fun getTrackById(trackId: Int): FavouriteTrackEntity?
}