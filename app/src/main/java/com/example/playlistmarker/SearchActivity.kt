package com.example.playlistmarker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.data.mappers.TrackDtoMapper
import com.example.playlistmarker.domain.use_case.HistoryInteractor
import com.example.playlistmarker.presentation.mapper.TrackMapper
import com.example.playlistmarker.presentation.model.TrackInfo
import com.example.playlistmarker.presentation.presenter.SearchPresenter
import com.example.playlistmarker.presentation.utills.DebounceHelper
import com.example.playlistmarker.presentation.utills.HideKeyboardHelper
import com.example.playlistmarker.presentation.utills.SearchStateManager
import com.example.playlistmarker.presentation.view.SearchView
import com.example.playlistmarker.trackrecyclerview.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity(), SearchView {

    private lateinit var backToMainFromSearchActivity: MaterialToolbar
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var placeholder: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderButton: com.google.android.material.button.MaterialButton
    private lateinit var historySearchText: TextView
    private lateinit var historySearchButton: com.google.android.material.button.MaterialButton
    private lateinit var progressBar: ProgressBar

    private val historyInteractor: HistoryInteractor by lazy { Creator.provideHistoryInteractor() }
    private val presenter: SearchPresenter by lazy { Creator.provideSearchPresenter() }
    private val clickDebounceHelper: DebounceHelper by lazy { Creator.provideClickDebounceHelper() }
    private val searchDebounceHelper: DebounceHelper by lazy { Creator.provideSearchDebounceHelper() }
    private val searchStateManager: SearchStateManager by lazy { Creator.provideSearchStateManager() }
    private val hideKeyboardHelper: HideKeyboardHelper by lazy { Creator.provideHideKeyboardHelper() }

    private val searchList = ArrayList<TrackInfo>()
    private val historyTrack = ArrayList<TrackInfo>()
    private val searchAdapter by lazy { TrackAdapter(searchList, ::onTrackSelected) }
    private val historyAdapter by lazy { TrackAdapter(historyTrack, ::onTrackSelected) }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchStateManager.saveSearchState(searchEditText.text.toString(), searchList, historyTrack)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchStateManager.restoreSearchState { text, searchHistory, history ->
            searchEditText.setText(text)
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

        initView()
        setupListeners()

        tracksRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksRecyclerView.adapter = searchAdapter

        presenter.attachView(this)
        presenter.loadHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView(this)
    }

    private fun initView() {
        backToMainFromSearchActivity = findViewById(R.id.activitySearchToolbar)
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        tracksRecyclerView = findViewById(R.id.recyclerView)
        placeholder = findViewById(R.id.placeholderError)
        placeholderImage = findViewById(R.id.placeholderErrorImage)
        placeholderText = findViewById(R.id.placeholderErrorText)
        placeholderButton = findViewById(R.id.placeholderErrorButton)
        historySearchText = findViewById(R.id.historySearchTextView)
        historySearchButton = findViewById(R.id.historySearchButtonView)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        backToMainFromSearchActivity.setNavigationOnClickListener {
            finish()
        }

        historySearchButton.setOnClickListener {
            historyInteractor.clearHistory()
            historyTrack.clear()
            historyAdapter.notifyDataSetChanged()
            historySetVisibility(false)
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            searchList.clear()
            searchEditText.clearFocus()
            searchAdapter.notifyDataSetChanged()
            hideKeyboardHelper.hideKeyboard(clearButton)
            historySetVisibility(false)
        }

        placeholderButton.setOnClickListener {
            presenter.searchTrack(searchEditText.text.toString())
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historySetVisibility(hasFocus && searchEditText.text.isEmpty() && historyTrack.isNotEmpty())
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleSearchTextChange(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun placeholderSetVisibility(isHidden: Boolean, text: String = "", imageRes: Int = 0, textRes: Int = 0) {
        if (isHidden) {
            placeholder.gone()
            tracksRecyclerView.show()
        } else {
            placeholder.show()
            tracksRecyclerView.gone()
            placeholderButton.apply {
                if (text.isEmpty()) gone() else show()
            }
            placeholderImage.setImageResource(imageRes)
            placeholderText.setText(textRes)
        }
    }

    private fun historySetVisibility(isVisible: Boolean) {
        if (isVisible) {
            tracksRecyclerView.adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
            historySearchText.show()
            tracksRecyclerView.show()
            historySearchButton.show()
        } else {
            tracksRecyclerView.adapter = searchAdapter
            searchAdapter.notifyDataSetChanged()
            historySearchText.gone()
            tracksRecyclerView.gone()
            historySearchButton.gone()
        }
    }

    private fun onTrackSelected(trackInfo: TrackInfo) {
        if (clickDebounce()) {
            val track = TrackMapper.mapToDomain(trackInfo)
            historyInteractor.addTrackHistory(track)
            val trackDto = TrackDtoMapper.toDto(track)
            startActivity(Intent(this, AudioPlayerActivity::class.java).apply {
                putExtra("track", trackDto)
            })
        }
    }

    private fun clickDebounce(): Boolean {
        return clickDebounceHelper.clickDebounceRun()
    }

    private fun searchDebounce() {
        searchDebounceHelper.searchDebounceRun { presenter.searchTrack(searchEditText.text.toString()) }
    }

    private fun handleSearchTextChange(s: CharSequence?) {
        clearButton.isVisible = !s.isNullOrEmpty()


        if(searchEditText.hasFocus() && s?.isEmpty() == true) {
            if (historyTrack.isNotEmpty()) {
                historySetVisibility(true)
            } else {
                searchList.clear()
                searchAdapter.notifyDataSetChanged()
            }
        } else {
            searchDebounce()
            historySetVisibility(false)
        }

        if (s.isNullOrEmpty()) {
            placeholderSetVisibility(true)
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

    override fun showTracks(track: List<TrackInfo>) {
        runOnUiThread {
            searchList.clear()
            searchList.addAll(track)
            searchAdapter.notifyDataSetChanged()
            placeholderSetVisibility(true)
        }
    }

    override fun showLoading(isLoading: Boolean) {
        progressBar.isVisible = isLoading
    }

    override fun showNotFound() {
        placeholderSetVisibility(
            isHidden = false,
            text = "",
            imageRes = R.drawable.ic_placeholder_not_found,
            textRes = R.string.not_found_songs
        )
    }

    override fun showErrorInternet() {
        placeholderSetVisibility(
            isHidden = false,
            text = getString(R.string.internet_problems),
            imageRes = R.drawable.ic_placeholder_internet,
            textRes = R.string.internet_problems
        )
    }

    override fun showHistory(trackHistory: List<TrackInfo>) {
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