package com.example.playlistmarker.data.db.sharedpreferences

import android.content.SharedPreferences

class AddedAtStorage(private val sharedPreferences: SharedPreferences) {


    fun saveAddedTime(trackId: Long, time: Long = System.currentTimeMillis()) {
        sharedPreferences.edit().putLong("added_at_$trackId", time).apply()
    }

    fun getAddedTime(trackId: Long): Long {
        return sharedPreferences.getLong("added_at_$trackId", 0L)
    }

    fun removeAddedTime(trackId: Long) {
        sharedPreferences.edit().remove("added_at_$trackId").apply()
    }
}