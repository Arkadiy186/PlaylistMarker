package com.example.playlistmarker.data.db.converters

import com.example.playlistmarker.data.db.entitys.TrackEntity
import com.example.playlistmarker.domain.db.model.Track

class TrackDbConverter {

    fun mapToDomain(trackEntity: TrackEntity): Track {
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

    fun mapToData(track: Track): TrackEntity {
        return TrackEntity(
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