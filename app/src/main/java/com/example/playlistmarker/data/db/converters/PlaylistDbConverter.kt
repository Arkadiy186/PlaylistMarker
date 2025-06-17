package com.example.playlistmarker.data.db.converters

import com.example.playlistmarker.data.db.entitys.PlaylistEntity
import com.example.playlistmarker.domain.db.model.Playlist

class PlaylistDbConverter {

    fun mapToDomain(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.playlistId,
            name = playlistEntity.name,
            description = playlistEntity.description,
            pathPictureCover = playlistEntity.pathPictureCover,
            listIdTracks = emptyList(),
            counterTracks = playlistEntity.counterTracks
        )
    }

    fun mapToData(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.id,
            name = playlist.name,
            description = playlist.description,
            pathPictureCover = playlist.pathPictureCover,
            counterTracks = playlist.counterTracks
        )
    }
}