package com.example.playlistmarker.presentation.utills

import android.os.Handler
import android.os.Looper
import com.example.playlistmarker.ui.searchactivity.SearchActivity.Companion.CLICK_DEBOUNCE_DELAY

class DebounceHelper(private val delayMillis: Long) {
    private var isAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var lastRunnable: Runnable? = null

    fun clickDebounceRun(): Boolean {
        if (isAllowed) {
            isAllowed = false
            handler.postDelayed({isAllowed = true}, CLICK_DEBOUNCE_DELAY)
            return true
        }
        return false
    }

    fun searchDebounceRun(action: () -> Unit) {
        lastRunnable?.let { handler.removeCallbacks(it) }
        val runnable = Runnable { action() }
        lastRunnable = runnable
        handler.postDelayed(runnable, delayMillis)
    }
}