package com.example.playlistmarker.data.network

import com.example.playlistmarker.data.model.Response
import com.example.playlistmarker.data.repository.RetrofitClient

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