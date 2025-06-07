package com.example.playlistmarker.data.db.impl

import com.example.playlistmarker.data.db.converters.TrackDbConverter
import com.example.playlistmarker.data.db.TrackDatabase
import com.example.playlistmarker.data.db.entitys.TrackEntity
import com.example.playlistmarker.data.db.sharedpreferences.AddedAtStorage
import com.example.playlistmarker.domain.db.repository.TrackDbRepository
import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackDbRepositoryImpl(
    private val trackDatabase: TrackDatabase,
    private val dbConverters: TrackDbConverter,
    private val addedAtStorage: AddedAtStorage
) : TrackDbRepository {

    override suspend fun insertTrack(track: Track) {
        val existingTrack = trackDatabase.trackDao().getTrackById(track.id)
        if (existingTrack == null) {
            addedAtStorage.saveAddedTime(track.id, System.currentTimeMillis())
            trackDatabase.trackDao().insertTrack(convertTracksToData(track))
        }
    }

    override suspend fun deleteTrack(track: Track) {
        val entity = trackDatabase.trackDao().getTrackById(track.id)
        if (entity != null) {
            addedAtStorage.removeAddedTime(track.id)
            trackDatabase.trackDao().deleteTrack(entity)
        }
    }

    override fun getFavouriteTracks(): Flow<List<Track>> {
        return trackDatabase.trackDao().getTracks()
            .map { entityList ->
                converterTracksToDomain(entityList)
            }
    }

    override fun getAllFavouriteTracks(): Flow<List<Int>> {
        return trackDatabase.trackDao().getIdTracks()
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