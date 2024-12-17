package com.example.playlistmarker.trackAPI

import com.example.playlistmarker.trackrecyclerview.Track
import com.google.gson.annotations.SerializedName

class TrackResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<Track>
)