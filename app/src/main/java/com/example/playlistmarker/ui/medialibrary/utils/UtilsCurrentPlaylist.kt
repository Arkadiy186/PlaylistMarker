package com.example.playlistmarker.ui.medialibrary.utils

import android.content.Context
import android.util.TypedValue

class UtilsCurrentPlaylist {
    fun parseTrackTimeMillis(time: String): Long {
        val parts = time.split(":")
        if (parts.size != 2) return 0L
        val minutes = parts[0].toLongOrNull() ?: 0L
        val seconds = parts[1].toLongOrNull() ?: 0L
        return (minutes * 60 + seconds) * 1000
    }

    fun dpToPx(dp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}