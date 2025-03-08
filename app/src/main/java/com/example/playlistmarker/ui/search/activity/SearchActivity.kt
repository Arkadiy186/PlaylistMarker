package com.example.playlistmarker.ui.search.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmarker.ui.audioplayer.activity.AudioPlayerActivity
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.databinding.ActivitySearchBinding
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.example.playlistmarker.ui.search.ui_state.UiStateHandler
import com.example.playlistmarker.ui.search.utills.DebounceHandler
import com.example.playlistmarker.ui.search.utills.HideKeyboardHelper
import com.example.playlistmarker.ui.search.viewmodel.searchviewmodel.SearchView
import com.example.playlistmarker.ui.search.recyclerview.TrackAdapter
import com.example.playlistmarker.ui.search.ui_state.UiHistoryHandler
import com.example.playlistmarker.ui.search.ui_state.UiHistoryHandlerImpl
import com.example.playlistmarker.ui.search.ui_state.UiStateHandlerImpl
import com.example.playlistmarker.ui.search.viewmodel.historyviewmodel.HistoryViewModel
import com.example.playlistmarker.ui.search.viewmodel.searchviewmodel.SearchViewModel
import com.example.playlistmarker.ui.search.viewmodel.searchviewmodel.UiState

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var uiHistoryHandler: UiHistoryHandler
    private lateinit var uiStateHandler: UiStateHandler

    private val historyInteractor: HistoryInteractor by lazy { Creator.provideHistoryInteractor() }
    private val debounceHandler: DebounceHandler by lazy { Creator.provideDebounceHandler() }
    private val hideKeyboardHelper: HideKeyboardHelper by lazy { Creator.provideHideKeyboardHelper() }
    private val searchStateInteractor: SearchStateInteractor by lazy { Creator.provideSearchStateInteractor() }

    private val searchList = ArrayList<TrackInfoDetails>()
    private val historyTrack = ArrayList<TrackInfoDetails>()
    private val searchAdapter by lazy { TrackAdapter(searchList, ::onTrackSelected) }
    private val historyAdapter by lazy { TrackAdapter(historyTrack, ::onTrackSelected) }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchStateInteractor.saveSearchState(binding.searchEditText.text.toString(), searchList, historyTrack)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchStateInteractor.restoreSearchState { text, searchHistory, history ->
            binding.searchEditText.setText(text)
            searchList.clear()
            searchList.addAll(searchHistory)
            historyTrack.clear()
            historyTrack.addAll(history)
            historyAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        historyViewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        uiHistoryHandler = UiHistoryHandlerImpl(binding, this, historyAdapter, searchAdapter)
        uiStateHandler = UiStateHandlerImpl(binding, this)

        setupListeners()

        binding.clearButton.isVisible = !binding.searchEditText.text.isNullOrEmpty()

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = searchAdapter

        observeViewModels()
    }

    private fun setupListeners() {
        binding.activitySearchToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.historySearchButtonView.setOnClickListener {
            historyViewModel.clearHistory()
            historyTrack.clear()
            historyAdapter.notifyDataSetChanged()
            uiHistoryHandler.historySetVisibility(false)
        }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")
            searchList.clear()
            binding.searchEditText.clearFocus()
            searchAdapter.notifyDataSetChanged()
            hideKeyboardHelper.hideKeyboard(binding.clearButton)
            uiHistoryHandler.historySetVisibility(false)
        }

        binding.placeholderErrorButton.setOnClickListener {
            uiStateHandler.placeholderSetVisibility(isHidden = true)
            searchViewModel.searchTrack(binding.searchEditText.text.toString())
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {

                historyViewModel.loadHistory()

                historyViewModel.historyState.observe(this) { history ->

                    historyTrack.clear()
                    historyTrack.addAll(history)
                    historyAdapter.notifyDataSetChanged()

                    val shouldShowHistory = history.isNotEmpty()
                    uiHistoryHandler.historySetVisibility(shouldShowHistory)
                }
            } else {
                uiHistoryHandler.historySetVisibility(false)
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("UiHistoryHandler", "textChanged")
                handleSearchTextChange(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun onTrackSelected(trackInfo: TrackInfoDetails) {
        if (debounceHandler.handleClickDebounce {
                val track = TrackInfoDetailsMapper.mapToDomain(trackInfo)
                historyInteractor.addTrackHistory(track)

                Log.d("SearchActivity", "Sending track info to AudioPlayerActivity: $trackInfo")
                startActivity(Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra("track", trackInfo)
                })
                true
            }) {
        }
    }

    private fun handleSearchTextChange(s: CharSequence?) {
        binding.clearButton.isVisible = !s.isNullOrEmpty()


        if(binding.searchEditText.hasFocus() && s?.isEmpty() == true) {
            if (historyTrack.isNotEmpty()) {
                uiHistoryHandler.historySetVisibility(true)
            } else {
                searchList.clear()
                searchAdapter.notifyDataSetChanged()
            }
        } else {
            debounceHandler.handleSearchDebounce(s.toString()) {query ->
                searchViewModel.searchTrack(query)
            }
            uiHistoryHandler.historySetVisibility(false)
        }

        if (s.isNullOrEmpty()) {
            uiStateHandler.placeholderSetVisibility(true)
        }
    }

    private fun observeViewModels() {
        searchViewModel.searchState.observe(this) { searchState ->
            when (searchState) {
                is UiState.NotFound -> {
                    uiStateHandler.showLoading(false)
                    uiStateHandler.showNotFound()
                }
                is UiState.ErrorInternet -> {
                    uiStateHandler.showLoading(false)
                    uiStateHandler.showErrorInternet(searchState.message)
                }
                is UiState.Loading -> uiStateHandler.showLoading(searchState.isLoading)
                is UiState.Content -> {
                    uiStateHandler.showLoading(false)
                    showTracks(searchState.tracks)
                }
            }
        }
    }

    private fun showTracks(track: List<TrackInfoDetails>) {
        runOnUiThread {
            searchList.clear()
            searchList.addAll(track)
            searchAdapter.notifyDataSetChanged()
            uiStateHandler.placeholderSetVisibility(isHidden = true)
        }
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 3000L
    }
}