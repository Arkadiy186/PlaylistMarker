package com.example.playlistmarker.domain.search.impl

import com.example.playlistmarker.domain.search.model.Track
import com.example.playlistmarker.domain.search.repository.Resources
import com.example.playlistmarker.domain.search.repository.TrackRepository
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {
    override fun searchTrack(query: String): Flow<Pair<List<Track>?, String?>> {
        return trackRepository.searchTrack(query).map { result ->
            when(result) {
                is Resources.Success -> {
                    Pair(result.data, null)
                }
                is Resources.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}