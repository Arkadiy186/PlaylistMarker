package com.example.playlistmarker.ui.medialibrary.utils

import android.content.Context
import android.content.Intent
import android.util.TypedValue

class UtilsCurrentPlaylist {
    fun parseTrackTimeMillis(time: String): Long {
        val parts = time.split(":")
        if (parts.size != 2) return 0L
        val minutes = parts[0].toLongOrNull() ?: 0L
        val seconds = parts[1].toLongOrNull() ?: 0L
        return (minutes * 60 + seconds) * 1000
    }

    fun formatDuration(durationMillis: Long): String {
        val minutes = (durationMillis / 1000) / 60
        val seconds = (durationMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun dpToPx(dp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    fun shareApp(context: Context, text: String, title: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, title))
    }
}