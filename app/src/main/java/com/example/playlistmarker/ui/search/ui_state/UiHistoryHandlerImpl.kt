package com.example.playlistmarker.ui.search.ui_state

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmarker.databinding.FragmentSearchBinding
import com.example.playlistmarker.ui.search.recyclerview.TrackAdapter

class UiHistoryHandlerImpl(
    private val binding: FragmentSearchBinding,
    private val fragment: Fragment,
    private val historyAdapter: TrackAdapter,
    private val searchAdapter: TrackAdapter
) : UiHistoryHandler {
    override fun historySetVisibility(isVisible: Boolean) {
        fragment.requireActivity().runOnUiThread {
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