package com.example.playlistmarker.data.search.sharedpreferences

import android.content.SharedPreferences
import com.example.playlistmarker.data.search.model.TrackDto
import com.google.gson.Gson

class HistorySearch(private val sharedPreferences: SharedPreferences) {

    fun addTrackHistory(track: TrackDto) {
        val currentHistory = getHistory().toMutableList()
        if (currentHistory.contains(track)) {
            currentHistory.remove(track)
        }
        currentHistory.add(0, track)

        if (currentHistory.size > HISTORY_MAX_SIZE) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }
        saveHistory(currentHistory)
    }

    fun getHistory(): List<TrackDto> {
        val historyJson = sharedPreferences.getString(HISTORY_KEY, null)
        return if (historyJson.isNullOrEmpty()) {
            mutableListOf()
        } else {
            Gson().fromJson(historyJson, Array<TrackDto>::class.java).toList()
        }
    }

    fun clearHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(history: List<TrackDto>) {
        val historyJson = Gson().toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, historyJson).apply()
    }

    companion object {
        private const val HISTORY_KEY = "history_key"
        private const val HISTORY_MAX_SIZE = 10
    }
}