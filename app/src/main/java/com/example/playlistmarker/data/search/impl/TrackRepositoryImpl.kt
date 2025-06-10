package com.example.playlistmarker.data.search.impl

import com.example.playlistmarker.data.db.FavouriteTrackDatabase
import com.example.playlistmarker.data.mappers.TrackDtoMapper
import com.example.playlistmarker.data.search.model.TrackResponse
import com.example.playlistmarker.data.search.network.RetrofitClient
import com.example.playlistmarker.data.db.sharedpreferences.AddedAtStorage
import com.example.playlistmarker.domain.db.model.Track
import com.example.playlistmarker.domain.search.repository.Resources
import com.example.playlistmarker.domain.search.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val retrofitClient: RetrofitClient,
    private val appDatabase: FavouriteTrackDatabase,
    private val addedAtStorage: AddedAtStorage
) : TrackRepository {
    override fun searchTrack(query: String): Flow<Resources<List<Track>>> = flow {
        val response = retrofitClient.doRequest(query)

        when(response.resultCode) {
            -1 -> {
                emit(Resources.Error("Проверьте подключение к интеренету"))
            }
            200 -> {
                val favoriteIds = appDatabase.favouriteTrackDao().getIdTracks().first()
                val trackResponse = response as TrackResponse
                val domainTracks = trackResponse.results.map { dto ->
                    TrackDtoMapper.mapToDomain(dto)
                }
                val updatedTracks = domainTracks.map { track ->
                    track.copy(
                        isFavourite = favoriteIds.contains(track.id),
                        addedAt = addedAtStorage.getAddedTime(track.id))
                }
                emit(Resources.Success(updatedTracks))
            }
            else -> {
                emit(Resources.Error("Ошибка сервера"))
            }
        }
    }
}