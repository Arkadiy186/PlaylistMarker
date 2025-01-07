package com.example.playlistmarker.sharedPreferencesUtills

import android.content.SharedPreferences
import com.example.playlistmarker.trackrecyclerview.Track
import com.google.gson.Gson

class HistorySearch(private val sharedPreferences: SharedPreferences) {
    companion object{
        private const val HISTORY_KEY = "history_key"
        private const val HISTORY_MAX_SIZE = 10
    }

    fun addTrackHistory(track: Track) {
        val currentHistory = getHistory()
        if (currentHistory.contains(track)) {
            currentHistory.remove(track)
        }
        currentHistory.add(0, track)

        if (currentHistory.size > HISTORY_MAX_SIZE) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }
        saveHistory(currentHistory)
    }

    fun getHistory(): MutableList<Track> {
        val historyJson = sharedPreferences.getString(HISTORY_KEY, null)
        return if (historyJson.isNullOrEmpty()) {
            mutableListOf()
        } else {
            Gson().fromJson(historyJson, Array<Track>::class.java).toMutableList()
        }
    }

    fun clearHistory() {
        saveHistory(mutableListOf())
    }

    private fun saveHistory(history: MutableList<Track>) {
        val historyJson = Gson().toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, historyJson).apply()
    }

    fun removeTrackFromHistory(track: Track) {
        val currentHistory = getHistory()
        currentHistory.remove(track)
        saveHistory(currentHistory)
    }
}