package com.example.playlistmarker.data.db.converters

import androidx.room.TypeConverter
import com.example.playlistmarker.data.db.entitys.PlaylistEntity
import com.example.playlistmarker.domain.db.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromList(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun toList(data: String): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(data, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun mapToDomain(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            pathPictureCover = playlistEntity.pathPictureCover,
            listIdTracks = playlistEntity.listIdTracks,
            counterTracks = playlistEntity.counterTracks
        )
    }

    fun mapToData(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            pathPictureCover = playlist.pathPictureCover,
            listIdTracks = playlist.listIdTracks,
            counterTracks = playlist.counterTracks
        )
    }
}