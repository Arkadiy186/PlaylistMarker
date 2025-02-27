package com.example.playlistmarker.presentation.mapper

import com.example.playlistmarker.domain.model.Track
import com.example.playlistmarker.presentation.model.TrackInfoDetails

object TrackInfoDetailsMapper {
    fun map(track: Track): TrackInfoDetails {
        return TrackInfoDetails(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun mapToDomain(trackInfo: TrackInfoDetails): Track {
        return Track(
            trackName = trackInfo.trackName,
            artistName = trackInfo.artistName,
            trackTime = trackInfo.trackTime,
            artworkUrl100 = trackInfo.artworkUrl100,
            collectionName = trackInfo.collectionName,
            releaseDate = trackInfo.releaseDate,
            primaryGenreName = trackInfo.primaryGenreName,
            country = trackInfo.country,
            previewUrl = trackInfo.previewUrl
        )
    }
}