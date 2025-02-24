package com.example.playlistmarker.data.network

import com.example.playlistmarker.data.model.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackAPI {
    @GET("/search?entity=song")
    fun search(@Query("term") query: String) : Call<TrackResponse>
}