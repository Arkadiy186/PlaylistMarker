package com.example.playlistmarker.domain.db.use_cases

import com.example.playlistmarker.domain.db.model.Playlist
import kotlinx.coroutines.flow.Flow


interface PlaylistDbInteractor {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylistId(playlistId: Long): Flow<Playlist>
    fun getPlaylists(): Flow<List<Playlist>>
}