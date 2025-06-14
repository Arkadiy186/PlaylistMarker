package com.example.playlistmarker.data.db.impl

import com.example.playlistmarker.data.db.PlaylistDataBase
import com.example.playlistmarker.data.db.PlaylistTrackDatabase
import com.example.playlistmarker.data.db.converters.PlaylistDbConverter
import com.example.playlistmarker.data.db.converters.PlaylistTrackDbConverter
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.model.Track
import com.example.playlistmarker.domain.db.repository.PlaylistDbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistDbRepositoryImpl(
    private val playlistDataBase: PlaylistDataBase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDatabase: PlaylistTrackDatabase,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter
) : PlaylistDbRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDataBase.playlistDao().insertPlaylist(playlistDbConverter.mapToData(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDataBase.playlistDao().deletePlaylist(playlistDbConverter.mapToData(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDataBase.playlistDao().updatePlaylist(playlistDbConverter.mapToData(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDataBase.playlistDao().getPlaylists()
            .map { entityList ->
                entityList.map { track ->
                    playlistDbConverter.mapToDomain(track)
                }
            }
    }

    override fun getPlaylistId(playlistId: Long): Flow<Playlist> {
        return playlistDataBase.playlistDao()
            .getPlaylistById(playlistId)
            .map { entity ->
                entity.let { playlistDbConverter.mapToDomain(it) }
            }
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return playlistTrackDatabase.playlistTrackDao()
            .getTracksForPlaylist(playlistId).map { list ->
                list.map { entity ->
                    playlistTrackDbConverter.mapToDomain(entity)
                }
            }
    }

    override suspend fun insertTrack(track: Track) {
        playlistTrackDatabase.playlistTrackDao().insertPlaylistTrack(playlistTrackDbConverter.mapToData(track))
    }
}