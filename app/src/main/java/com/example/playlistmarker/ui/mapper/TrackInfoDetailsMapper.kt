package com.example.playlistmarker.ui.mapper

import com.example.playlistmarker.domain.search.model.Track
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

object TrackInfoDetailsMapper {
    fun map(track: Track): TrackInfoDetails {
        return TrackInfoDetails(
            id = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavourite = track.isFavourite
        )
    }

    fun mapToDomain(trackInfo: TrackInfoDetails): Track {
        return Track(
            id = trackInfo.id,
            trackName = trackInfo.trackName,
            artistName = trackInfo.artistName,
            trackTime = trackInfo.trackTime,
            artworkUrl100 = trackInfo.artworkUrl100,
            collectionName = trackInfo.collectionName,
            releaseDate = trackInfo.releaseDate,
            primaryGenreName = trackInfo.primaryGenreName,
            country = trackInfo.country,
            previewUrl = trackInfo.previewUrl,
            isFavourite = trackInfo.isFavourite
        )
    }
}