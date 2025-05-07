package com.example.playlistmarker.data.search.network

import com.example.playlistmarker.data.search.model.Response
import com.example.playlistmarker.domain.search.repository.utills.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitClientImpl(private val trackAPI: TrackAPI, private val networkRepository: NetworkRepository) : RetrofitClient {
    override suspend fun doRequest(query: String): Response {
        if (!networkRepository.isInternetAvailable()) {
            return Response().apply { resultCode = -1 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = trackAPI.search(query)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
    }
}