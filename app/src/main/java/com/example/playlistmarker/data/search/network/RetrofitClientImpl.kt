package com.example.playlistmarker.data.search.network

import com.example.playlistmarker.data.search.model.Response

class RetrofitClientImpl : RetrofitClient {
    override fun doRequest(query: String): Response {
        try {
            val response = RetrofitApiService.itunesApiService.search(query).execute()

            val body = response.body() ?: Response()
            return body.apply { resultCode = response.code() }
        }catch (e:Exception) {
            return Response().apply { resultCode = 400 }
        }
    }
}