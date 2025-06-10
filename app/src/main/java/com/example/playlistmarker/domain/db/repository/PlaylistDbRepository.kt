package com.example.playlistmarker.domain.db.repository

import com.example.playlistmarker.domain.db.model.Playlist
import com.example.playlistmarker.domain.db.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistDbRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun insertTrack(track: Track)
}