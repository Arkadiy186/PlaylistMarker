package com.example.playlistmarker.data.search.network

import com.example.playlistmarker.data.search.model.Response

interface RetrofitClient {
    suspend fun doRequest(query: String): Response
}