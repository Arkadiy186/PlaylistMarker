package com.example.playlistmarker.data.db

import com.example.playlistmarker.data.db.entitys.TrackEntity
import com.example.playlistmarker.domain.search.model.Track

class DbConverters {

    fun mapToDomain(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.id,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTime,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl)
    }

    fun mapToData(track: Track): TrackEntity {
        return TrackEntity(
            track.id,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)
    }
}