package com.example.playlistmarker.ui.search.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarker.ui.audioplayer.activity.AudioPlayerActivity
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.databinding.ActivitySearchBinding
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.example.playlistmarker.ui.search.viewmodel.SearchPresenter
import com.example.playlistmarker.ui.search.ui_state.UiStateHandler
import com.example.playlistmarker.ui.search.utills.DebounceHandler
import com.example.playlistmarker.ui.search.utills.HideKeyboardHelper
import com.example.playlistmarker.ui.search.viewmodel.SearchView
import com.example.playlistmarker.ui.search.recyclerview.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity(), SearchView {

    private lateinit var binding: ActivitySearchBinding


    private val historyInteractor: HistoryInteractor by lazy { Creator.provideHistoryInteractor() }
    private val presenter: SearchPresenter by lazy { Creator.provideSearchPresenter(binding.placeholderError, binding.placeholderErrorText, binding.placeholderErrorImage, binding.recyclerView, binding.placeholderErrorButton, binding.progressBar, this) }
    private val debounceHandler: DebounceHandler by lazy { Creator.provideDebounceHandler() }
    private val hideKeyboardHelper: HideKeyboardHelper by lazy { Creator.provideHideKeyboardHelper() }
    private val searchStateInteractor: SearchStateInteractor by lazy { Creator.provideSearchStateInteractor() }
    private val uiStateHandler: UiStateHandler by lazy { Creator.provideUiStateHandler(binding.placeholderError, binding.placeholderErrorText, binding.placeholderErrorImage, binding.recyclerView, binding.placeholderErrorButton, binding.progressBar,this) }

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

        setupListeners()

        binding.clearButton.isVisible = !binding.searchEditText.text.isNullOrEmpty()

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = searchAdapter

        presenter.attachView(this)
        presenter.loadHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView(this)
    }

    private fun setupListeners() {
        binding.activitySearchToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.historySearchButtonView.setOnClickListener {
            historyInteractor.clearHistory()
            historyTrack.clear()
            historyAdapter.notifyDataSetChanged()
            historySetVisibility(false)
        }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")
            searchList.clear()
            binding.searchEditText.clearFocus()
            searchAdapter.notifyDataSetChanged()
            hideKeyboardHelper.hideKeyboard(binding.clearButton)
            historySetVisibility(false)
        }

        binding.placeholderErrorButton.setOnClickListener {
            presenter.searchTrack(binding.searchEditText.text.toString())
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historySetVisibility(hasFocus && binding.searchEditText.text.isEmpty() && historyTrack.isNotEmpty())
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleSearchTextChange(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun historySetVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.recyclerView.adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
            binding.historySearchTextView.show()
            binding.recyclerView.show()
            binding.historySearchButtonView.show()
        } else {
            binding.recyclerView.adapter = searchAdapter
            searchAdapter.notifyDataSetChanged()
            binding.historySearchTextView.gone()
            binding.recyclerView.gone()
            binding.historySearchButtonView.gone()
        }
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
                historySetVisibility(true)
            } else {
                searchList.clear()
                searchAdapter.notifyDataSetChanged()
            }
        } else {
            debounceHandler.handleSearchDebounce(s.toString()) {query ->
                presenter.searchTrack(query)
            }
            historySetVisibility(false)
        }

        if (s.isNullOrEmpty()) {
            uiStateHandler.placeholderSetVisibility(true)
        }

       presenter.loadHistory()
    }

    private fun View.show() {
        visibility = View.VISIBLE
    }

    private fun View.gone() {
        visibility = View.GONE
    }

    private fun View.hide() {
        visibility = View.INVISIBLE
    }

    override fun showTracks(track: List<TrackInfoDetails>) {
        runOnUiThread {
            searchList.clear()
            searchList.addAll(track)
            searchAdapter.notifyDataSetChanged()
            uiStateHandler.placeholderSetVisibility(isHidden = true)
        }
    }

    override fun showLoading(isLoading: Boolean) {
        runOnUiThread {
            uiStateHandler.showLoading(isLoading)
        }
    }

    override fun showNotFound() {
        runOnUiThread {
            uiStateHandler.showNotFound()
        }
    }

    override fun showErrorInternet() {
        runOnUiThread {
            uiStateHandler.showErrorInternet(R.string.internet_problems)
        }
    }

    override fun showHistory(trackHistory: List<TrackInfoDetails>) {
        runOnUiThread {
            historyTrack.clear()
            historyTrack.addAll(trackHistory)
            historyAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 3000L
    }
}