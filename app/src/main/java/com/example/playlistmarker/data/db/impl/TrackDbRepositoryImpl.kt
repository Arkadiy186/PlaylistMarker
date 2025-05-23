package com.example.playlistmarker.data.db.impl

import com.example.playlistmarker.data.db.AppDatabase
import com.example.playlistmarker.data.db.DbConverters
import com.example.playlistmarker.data.db.entitys.TrackEntity
import com.example.playlistmarker.domain.db.repository.TrackDbRepository
import com.example.playlistmarker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackDbRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val dbConverters: DbConverters
) : TrackDbRepository {

    override suspend fun insertTrack(track: Track) {
        appDatabase.trackDao().insertTrack(convertTracksToData(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(convertTracksToData(track))
    }

    override fun getFavouriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(converterTracksToDomain(tracks))
    }

    override fun getAllFavouriteTracks(): Flow<List<Int>> = flow {
        emit(appDatabase.trackDao().getIdTracks())
    }

    private fun converterTracksToDomain(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> dbConverters.mapToDomain(track) }
    }

    private fun convertTracksToData(track: Track): TrackEntity {
        return dbConverters.mapToData(track)
    }
}