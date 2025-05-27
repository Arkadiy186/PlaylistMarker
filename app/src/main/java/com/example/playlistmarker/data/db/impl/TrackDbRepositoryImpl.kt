package com.example.playlistmarker.data.db.impl

import com.example.playlistmarker.data.db.AppDatabase
import com.example.playlistmarker.data.db.DbConverters
import com.example.playlistmarker.data.db.entitys.TrackEntity
import com.example.playlistmarker.data.db.sharedpreferences.AddedAtStorage
import com.example.playlistmarker.domain.db.repository.TrackDbRepository
import com.example.playlistmarker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackDbRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val dbConverters: DbConverters,
    private val addedAtStorage: AddedAtStorage
) : TrackDbRepository {

    override suspend fun insertTrack(track: Track) {
        val existingTrack = appDatabase.trackDao().getTrackById(track.id)
        if (existingTrack == null) {
            addedAtStorage.saveAddedTime(track.id, System.currentTimeMillis())
            appDatabase.trackDao().insertTrack(convertTracksToData(track))
        }
    }

    override suspend fun deleteTrack(track: Track) {
        val entity = appDatabase.trackDao().getTrackById(track.id)
        if (entity != null) {
            addedAtStorage.removeAddedTime(track.id)
            appDatabase.trackDao().deleteTrack(entity)
        }
    }

    override fun getFavouriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks()
            .map { entityList ->
                converterTracksToDomain(entityList)
            }
    }

    override fun getAllFavouriteTracks(): Flow<List<Int>> {
        return appDatabase.trackDao().getIdTracks()
    }

    private fun converterTracksToDomain(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track ->
            dbConverters.mapToDomain(track).copy(
                addedAt = addedAtStorage.getAddedTime(track.id)
            ) }
    }

    private fun convertTracksToData(track: Track): TrackEntity {
        return dbConverters.mapToData(track)
    }
}