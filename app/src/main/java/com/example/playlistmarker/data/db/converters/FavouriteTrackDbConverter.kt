package com.example.playlistmarker.data.db.converters

import com.example.playlistmarker.data.db.entitys.FavouriteTrackEntity
import com.example.playlistmarker.domain.db.model.Track

class FavouriteTrackDbConverter {

    fun mapToDomain(trackEntity: FavouriteTrackEntity): Track {
        return Track(
            id = trackEntity.id,
            playlistId = trackEntity.playlistId,
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

    fun mapToData(track: Track): FavouriteTrackEntity {
        return FavouriteTrackEntity(
            id = track.id,
            playlistId = track.playlistId,
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