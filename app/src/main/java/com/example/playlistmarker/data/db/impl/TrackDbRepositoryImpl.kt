package com.example.playlistmarker.data.db.impl

import com.example.playlistmarker.data.db.converters.FavouriteTrackDbConverter
import com.example.playlistmarker.data.db.FavouriteTrackDatabase
import com.example.playlistmarker.data.db.entitys.FavouriteTrackEntity
import com.example.playlistmarker.data.db.sharedpreferences.AddedAtStorage
import com.example.playlistmarker.domain.db.repository.TrackDbRepository
import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackDbRepositoryImpl(
    private val trackDatabase: FavouriteTrackDatabase,
    private val dbConverters: FavouriteTrackDbConverter,
    private val addedAtStorage: AddedAtStorage
) : TrackDbRepository {

    override suspend fun insertTrack(track: Track) {
        val existingTrack = trackDatabase.favouriteTrackDao().getTrackById(track.id)
        if (existingTrack == null) {
            addedAtStorage.saveAddedTime(track.id, System.currentTimeMillis())
            trackDatabase.favouriteTrackDao().insertTrack(convertTracksToData(track))
        }
    }

    override suspend fun deleteTrack(track: Track) {
        val entity = trackDatabase.favouriteTrackDao().getTrackById(track.id)
        if (entity != null) {
            addedAtStorage.removeAddedTime(track.id)
            trackDatabase.favouriteTrackDao().deleteTrack(entity)
        }
    }

    override fun getFavouriteTracks(): Flow<List<Track>> {
        return trackDatabase.favouriteTrackDao().getTracks()
            .map { entityList ->
                converterTracksToDomain(entityList)
            }
    }

    override fun getAllFavouriteTracks(): Flow<List<Int>> {
        return trackDatabase.favouriteTrackDao().getIdTracks()
    }

    private fun converterTracksToDomain(tracks: List<FavouriteTrackEntity>): List<Track> {
        return tracks.map { track ->
            dbConverters.mapToDomain(track).copy(
                addedAt = addedAtStorage.getAddedTime(track.id)
            ) }
    }

    private fun convertTracksToData(track: Track): FavouriteTrackEntity {
        return dbConverters.mapToData(track)
    }
}