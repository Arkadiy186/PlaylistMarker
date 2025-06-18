package com.example.playlistmarker.data.db.impl

import com.example.playlistmarker.data.db.AppDatabase
import com.example.playlistmarker.data.db.converters.FavouriteTrackDbConverter
import com.example.playlistmarker.data.db.entitys.FavouriteTrackEntity
import com.example.playlistmarker.data.db.sharedpreferences.AddedAtStorage
import com.example.playlistmarker.domain.db.repository.TrackDbRepository
import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackDbRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val dbConverters: FavouriteTrackDbConverter,
    private val addedAtStorage: AddedAtStorage
) : TrackDbRepository {

    override suspend fun insertTrack(track: Track) {
        val existingTrack = appDatabase.favouriteTrackDao().getTrackById(track.id)
        if (existingTrack == null) {
            addedAtStorage.saveAddedTime(track.id, System.currentTimeMillis())
            appDatabase.favouriteTrackDao().insertTrack(convertTracksToData(track))
        }
    }

    override suspend fun deleteTrack(track: Track) {
        val entity = appDatabase.favouriteTrackDao().getTrackById(track.id)
        if (entity != null) {
            addedAtStorage.removeAddedTime(track.id)
            appDatabase.favouriteTrackDao().deleteTrack(entity)
        }
    }

    override fun getAllFavouriteTrackDetails(): Flow<List<Track>> {
        return appDatabase.favouriteTrackDao().getTracks()
            .map { entityList ->
                converterTracksToDomain(entityList)
            }
    }

    override fun getFavouriteTrackIds(): Flow<List<Long>> {
        return appDatabase.favouriteTrackDao().getIdTracks()
    }

    private fun converterTracksToDomain(tracks: List<FavouriteTrackEntity>): List<Track> {
        return tracks.map { track ->
            dbConverters.mapToDomain(track).copy(
                addedAt = addedAtStorage.getAddedTime(track.trackId)
            ) }
    }

    private fun convertTracksToData(track: Track): FavouriteTrackEntity {
        return dbConverters.mapToData(track)
    }
}