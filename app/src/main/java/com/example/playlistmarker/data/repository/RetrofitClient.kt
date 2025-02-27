package com.example.playlistmarker.data.repository

import com.example.playlistmarker.data.model.Response

interface RetrofitClient {
    fun doRequest(query: String): Response
}