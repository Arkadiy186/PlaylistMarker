package com.example.playlistmarker.domain.db.impl

import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.repository.PlaylistDbRepository
import com.example.playlistmarker.domain.db.use_cases.PlaylistDbInteractor
import kotlinx.coroutines.flow.Flow

class PlaylistDbInteractorImpl(private val playlistDbRepository: PlaylistDbRepository) : PlaylistDbInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDbRepository.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDbRepository.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDbRepository.updatePlaylist(playlist)
    }

    override fun getPlaylistId(playlistId: Long): Flow<Playlist> {
        return playlistDbRepository.getPlaylistId(playlistId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDbRepository.getPlaylists()
    }
}