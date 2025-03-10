package com.example.playlistmarker.ui.search.ui_state

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.playlistmarker.databinding.ActivitySearchBinding
import com.example.playlistmarker.ui.search.recyclerview.TrackAdapter

class UiHistoryHandlerImpl(
    private val binding: ActivitySearchBinding,
    private val activity: AppCompatActivity,
    private val historyAdapter: TrackAdapter,
    private val searchAdapter: TrackAdapter
) : UiHistoryHandler {
    override fun historySetVisibility(isVisible: Boolean) {
        activity.runOnUiThread {
            if (isVisible) {
                Log.d("UiHistoryHandler", "isVisible: {$isVisible}")
                binding.recyclerView.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()
                binding.historySearchTextView.isVisible = true
                binding.recyclerView.isVisible = true
                binding.historySearchButtonView.isVisible = true
            } else {
                binding.recyclerView.adapter = searchAdapter
                searchAdapter.notifyDataSetChanged()
                binding.historySearchTextView.isVisible = false
                binding.recyclerView.isVisible = false
                binding.historySearchButtonView.isVisible = false
            }
        }
    }
}