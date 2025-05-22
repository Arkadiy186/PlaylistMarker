package com.example.playlistmarker.data.search.network

import com.example.playlistmarker.data.search.model.Response
import com.example.playlistmarker.domain.search.repository.utills.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitClientImpl(private val trackAPI: TrackAPI, private val networkRepository: NetworkRepository) : RetrofitClient {
    override suspend fun doRequest(query: String): Response {
        if (!networkRepository.isInternetAvailable()) {
            return Response().apply { resultCode = NOT_HAVE_INTERNET }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = trackAPI.search(query)
                response.apply { resultCode = RESULT_SUCCESS }
            } catch (e: Throwable) {
                Response().apply { resultCode = RESULT_NOT_SUCCESS }
            }
        }
    }

    companion object {
        const val NOT_HAVE_INTERNET = -1
        const val RESULT_SUCCESS = 200
        const val RESULT_NOT_SUCCESS = 500
    }
}