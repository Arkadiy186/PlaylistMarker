package com.example.playlistmarker.data.player.sharedpreferences

import android.content.SharedPreferences

class PositionTime(private val sharedPreferences: SharedPreferences) {

    private val POSITION_KEY = "current_position"

    fun getCurrentPosition(): Int {
        val savedPosition = sharedPreferences.getString(POSITION_KEY, "0")
        return savedPosition?.toIntOrNull() ?: 0
    }

    fun saveCurrentPosition(position: Int) {
        sharedPreferences.edit().putInt(POSITION_KEY, position).apply()
    }

    fun resetPosition() {
        sharedPreferences.edit().remove(POSITION_KEY).apply()
    }
}