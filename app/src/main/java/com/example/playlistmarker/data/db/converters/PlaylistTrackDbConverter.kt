package com.example.playlistmarker.data.db.converters

import com.example.playlistmarker.data.db.entitys.TrackPlaylistEntity
import com.example.playlistmarker.domain.db.model.Track

class PlaylistTrackDbConverter {
    fun mapToDomain(trackEntity: TrackPlaylistEntity): Track {
        return Track(
            id = trackEntity.trackId,
            trackName = "",
            artistName = "",
            trackTime = "",
            artworkUrl100 = "",
            collectionName = "",
            releaseDate = "",
            primaryGenreName = "",
            country = "",
            previewUrl = "")
    }

    fun mapToData(trackId: Long, playlistId: Long): TrackPlaylistEntity {
        return TrackPlaylistEntity(trackId = trackId, playlistId = playlistId)
    }
}