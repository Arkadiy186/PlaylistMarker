package com.example.playlistmarker.ui.mapper

import com.example.playlistmarker.domain.db.model.Track
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

object TrackInfoDetailsMapper {
    fun map(track: Track): TrackInfoDetails {
        return TrackInfoDetails(
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
            previewUrl = track.previewUrl,
            isFavourite = track.isFavourite,
            addedAt = track.addedAt
        )
    }

    fun mapToDomain(trackInfo: TrackInfoDetails): Track {
        return Track(
            id = trackInfo.id,
            playlistId = trackInfo.playlistId,
            trackName = trackInfo.trackName,
            artistName = trackInfo.artistName,
            trackTime = trackInfo.trackTime,
            artworkUrl100 = trackInfo.artworkUrl100,
            collectionName = trackInfo.collectionName,
            releaseDate = trackInfo.releaseDate,
            primaryGenreName = trackInfo.primaryGenreName,
            country = trackInfo.country,
            previewUrl = trackInfo.previewUrl,
            isFavourite = trackInfo.isFavourite,
            addedAt = trackInfo.addedAt
        )
    }
}