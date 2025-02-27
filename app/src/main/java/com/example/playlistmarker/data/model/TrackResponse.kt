package com.example.playlistmarker.data.model

import com.google.gson.annotations.SerializedName

class TrackResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
) : Response()