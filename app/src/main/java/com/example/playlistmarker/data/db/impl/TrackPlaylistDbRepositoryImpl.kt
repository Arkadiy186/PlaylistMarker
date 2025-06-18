package com.example.playlistmarker.data.db.impl

import android.util.Log
import com.example.playlistmarker.data.db.AppDatabase
import com.example.playlistmarker.data.db.converters.PlaylistTrackDbConverter
import com.example.playlistmarker.data.db.converters.TrackDbConverter
import com.example.playlistmarker.data.db.sharedpreferences.AddedAtStorage
import com.example.playlistmarker.domain.db.model.Track
import com.example.playlistmarker.domain.db.repository.TrackPlaylistDbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TrackPlaylistDbRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter,
    private val trackDbConverter: TrackDbConverter,
    private val addedAtStorage: AddedAtStorage
) : TrackPlaylistDbRepository {
    override suspend fun insertTrackIntoPlaylist(track: Track, id: Long) {
        val relationEntity = playlistTrackDbConverter.mapToData(track.id, id)
        appDatabase.trackPlaylistDao().insertPlaylistTrack(relationEntity)
        addedAtStorage.saveAddedTime(track.id, System.currentTimeMillis())

        val playlistEntity = appDatabase.playlistDao().getPlaylistById(id).first()
        val updatedPlaylist = playlistEntity.copy(counterTracks = playlistEntity.counterTracks + 1)
        appDatabase.playlistDao().updatePlaylist(updatedPlaylist)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        appDatabase.trackPlaylistDao().deleteTrackFromPlaylist(trackId, playlistId)
        addedAtStorage.removeAddedTime(trackId)

        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId).first()
        val updatedCounter = (playlistEntity.counterTracks - 1).coerceAtLeast(0)
        val updatedPlaylist = playlistEntity.copy(counterTracks = updatedCounter)
        appDatabase.playlistDao().updatePlaylist(updatedPlaylist)

        val listPlaylists = appDatabase.playlistDao().getPlaylists().first()
        var trackStillExists = false

        for (playlist in listPlaylists) {
            val tracks = appDatabase.trackPlaylistDao().getTracksForPlaylist(playlist.playlistId).first()
            if (tracks.any { it.trackId == trackId }) {
                trackStillExists = true
                break
            }
        }

        if (!trackStillExists) {
            appDatabase.trackPlaylistDao().deleteTrackById(trackId)
        }
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return appDatabase.trackPlaylistDao()
            .getTracksForPlaylist(playlistId).map { list ->
                list.map { entity ->
                    trackDbConverter.mapToDomain(entity)
                }.sortedByDescending {track ->
                    addedAtStorage.getAddedTime(track.id)
                }
            }
    }
}