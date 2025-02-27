package com.example.playlistmarker.data.repository

import com.example.playlistmarker.data.mappers.TrackDtoMapper
import com.example.playlistmarker.data.model.Response
import com.example.playlistmarker.data.model.TrackResponse
import com.example.playlistmarker.domain.model.Track
import com.example.playlistmarker.domain.repository.TrackRepository

class TrackRepositoryImpl(private val retrofitClient: RetrofitClient) : TrackRepository {
    override fun searchTrack(query: String): List<Track> {
        val response: Response = retrofitClient.doRequest(query)

        if (response.resultCode == 200) {
            return (response as TrackResponse).results.map { TrackDtoMapper.mapToDomain(it) }
        } else {
            return emptyList()
        }
    }
}