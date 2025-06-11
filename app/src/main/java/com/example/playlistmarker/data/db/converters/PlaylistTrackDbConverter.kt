package com.example.playlistmarker.data.db.converters

import com.example.playlistmarker.data.db.entitys.TrackPlaylistEntity
import com.example.playlistmarker.domain.db.model.Track

class PlaylistTrackDbConverter {
    fun mapToDomain(trackEntity: TrackPlaylistEntity): Track {
        return Track(
            id = trackEntity.id,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTime = trackEntity.trackTime,
            artworkUrl100 = trackEntity.artworkUrl100,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseDate,
            primaryGenreName = trackEntity.trackName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl)
    }

    fun mapToData(track: Track): TrackPlaylistEntity {
        return TrackPlaylistEntity(
            id = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl)
    }
}