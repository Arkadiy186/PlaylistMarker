package com.example.playlistmarker.presentation.utills

import android.content.SharedPreferences
import com.example.playlistmarker.presentation.model.TrackInfoDetails
import com.google.gson.Gson

class SearchStateManager(private val sharedPreferences: SharedPreferences) {
    fun saveSearchState(searchText: String, searchList: List<TrackInfoDetails>, historyList: List<TrackInfoDetails>) {
        sharedPreferences.edit()
            .putString("search_text", searchText)
            .putString("search_list", Gson().toJson(searchList))
            .putString("history_list", Gson().toJson(historyList))
            .apply()
    }

    fun restoreSearchState(callback: (String, List<TrackInfoDetails>, List<TrackInfoDetails>) -> Unit) {
        val searchText = sharedPreferences.getString("search_text", "") ?: ""
        val searchListJson = sharedPreferences.getString("search_list", "[]")
        val historyListJson = sharedPreferences.getString("history_list", "[]")

        val searchList = Gson().fromJson(searchListJson, Array<TrackInfoDetails>::class.java).toList()
        val historyList = Gson().fromJson(historyListJson, Array<TrackInfoDetails>::class.java).toList()

        callback(searchText, searchList, historyList)
    }
}