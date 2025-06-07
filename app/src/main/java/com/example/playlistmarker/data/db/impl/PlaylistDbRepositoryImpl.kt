package com.example.playlistmarker.data.db.impl

import com.example.playlistmarker.data.db.PlaylistDataBase
import com.example.playlistmarker.data.db.converters.PlaylistDbConverter
import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.repository.PlaylistDbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistDbRepositoryImpl(
    private val playlistDataBase: PlaylistDataBase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistDbRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDataBase.playlistDao().insertPlaylist(playlistDbConverter.mapToData(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDataBase.playlistDao().getPlaylistById(playlist.id)
        if (playlistEntity != null) {
            playlistDataBase.playlistDao().deletePlaylist(playlistDbConverter.mapToData(playlist))
        }
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
}