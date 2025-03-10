package com.example.playlistmarker.data.search.impl

import com.example.playlistmarker.data.mappers.TrackDtoMapper
import com.example.playlistmarker.data.search.network.RetrofitClient
import com.example.playlistmarker.data.search.model.Response
import com.example.playlistmarker.data.search.model.TrackResponse
import com.example.playlistmarker.domain.search.model.Track
import com.example.playlistmarker.domain.search.repository.TrackRepository

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