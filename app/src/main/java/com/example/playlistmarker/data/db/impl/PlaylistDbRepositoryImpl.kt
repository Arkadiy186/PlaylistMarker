package com.example.playlistmarker.data.db.impl

import com.example.playlistmarker.data.db.AppDatabase
import com.example.playlistmarker.data.db.converters.PlaylistDbConverter
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.repository.PlaylistDbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PlaylistDbRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
) : PlaylistDbRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.mapToData(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlistDbConverter.mapToData(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistDbConverter.mapToData(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists()
            .map { entityList ->
                entityList.map { track ->
                    playlistDbConverter.mapToDomain(track)
                }
            }
    }

    override fun getPlaylistId(playlistId: Long): Flow<Playlist> {
        return combine(
            appDatabase.playlistDao().getPlaylistById(playlistId),
            appDatabase.trackPlaylistDao().getTracksForPlaylist(playlistId)
        ) { playlistEntity, trackEntities ->
            val listTrackIds = trackEntities.map { it.trackId.toString() }
            Playlist(
                id = playlistEntity.playlistId,
                name = playlistEntity.name,
                description = playlistEntity.description,
                pathPictureCover = playlistEntity.pathPictureCover,
                listIdTracks = listTrackIds,
                counterTracks = listTrackIds.size
            )
        }
    }
}