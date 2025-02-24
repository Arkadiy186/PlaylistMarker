package com.example.playlistmarker.presentation.mapper

import com.example.playlistmarker.domain.model.Track
import com.example.playlistmarker.presentation.model.TrackInfo

object TrackMapper {
    fun map(track: Track): TrackInfo {
        return TrackInfo(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100
        )
    }

    fun mapToDomain(trackInfo: TrackInfo): Track {
        return Track(
            trackName = trackInfo.trackName,
            artistName = trackInfo.artistName,
            trackTime = trackInfo.trackTime,
            artworkUrl100 = trackInfo.artworkUrl100,
            collectionName = "",
            releaseDate = "",
            primaryGenreName = "",
            country = "",
            previewUrl = ""
        )
    }
}