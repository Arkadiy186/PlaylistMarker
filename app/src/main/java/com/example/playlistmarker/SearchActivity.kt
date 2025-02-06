package com.example.playlistmarker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.playlistmarker.sharedPreferencesUtills.HistorySearch
import com.example.playlistmarker.trackAPI.RetrofitApiService
import com.example.playlistmarker.trackAPI.TrackResponse
import com.example.playlistmarker.trackrecyclerview.Track
import com.example.playlistmarker.trackrecyclerview.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var historySearch: HistorySearch

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

    private val searchList = ArrayList<Track>()
    private val historyTrack = ArrayList<Track>()
    private var mainThreadHandler: Handler? = null
    private var isAllowed = true
    private val searchRunnable = Runnable { trackSearch(searchEditText.text.toString()) }

    private val searchAdapter = TrackAdapter(searchList) {
        historySearch.addTrackHistory(it)
        openAudioPlayer(it)
    }

    private val historyAdapter = TrackAdapter(historyTrack) {
        openAudioPlayer(it)
        mainThreadHandler?.postDelayed({
            historySearch.addTrackHistory(it)
            historyLoad()
        }, 800)
    }

    private var textValue: String = TEXT_DEF

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveSearchState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreSearchState(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences(SEARCH_NAME_PREFS, Context.MODE_PRIVATE)
        historySearch = HistorySearch(sharedPreferences)
        mainThreadHandler = Handler(Looper.getMainLooper())

        initView()

        setupListeners()

        clearButton.isVisible = !searchEditText.text.isNullOrEmpty()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleSearchTextChange(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        tracksRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksRecyclerView.adapter = searchAdapter

        historyLoad()
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
            onHistorySearchButtonClicked()
        }

        clearButton.setOnClickListener {
            onClearButtonClicked()
        }

        placeholderButton.setOnClickListener {
            onPlaceholderButtonClicked()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historySetVisibility(hasFocus && searchEditText.text.isEmpty() && historyTrack.isNotEmpty())
        }
    }

    private fun onHistorySearchButtonClicked() {
        historySearch.clearHistory()
        historyTrack.clear()
        historyAdapter.notifyDataSetChanged()
        historySetVisibility(false)
    }

    private fun onClearButtonClicked() {
        searchEditText.setText("")
        searchList.clear()
        searchEditText.clearFocus()
        searchAdapter.notifyDataSetChanged()
        hideKeyboard(clearButton)

        historySetVisibility(historyTrack.isNotEmpty())
    }

    private fun onPlaceholderButtonClicked() {
        val query = searchEditText.text.toString()

        if (!isInternetAvailable()) {
            showErrorInternet()
            return
        }

        if (query.isNotEmpty()) {
            trackSearch(query)
        }
    }

    private fun historyLoad() {
        historyTrack.clear()
        historyTrack.addAll(historySearch.getHistory())
        Log.d("SearchActivity", "historyLoad: Loaded ${historyTrack.size} items")
        historyAdapter.notifyDataSetChanged()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun trackSearch(inputSong: String) {
        if (searchEditText.text.isNotEmpty()) {
            progressBarSetVisibility(true)
            historySetVisibility(false)
            RetrofitApiService.itunesApiService.search(inputSong).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    progressBarSetVisibility(false)
                    if (response.code() == 200) {
                        searchList.clear()
                        placeholderSetVisibility(isHidden = true)

                        if (response.body()?.results?.isNotEmpty() == true) {
                            searchList.addAll(response.body()?.results!!)
                            searchAdapter.notifyDataSetChanged()
                        } else {
                            showNotFound()
                        }
                    } else {
                        showErrorInternet()
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    progressBarSetVisibility(false)
                    showErrorInternet()
                }
            })
        }
    }

    private fun showErrorInternet() {
        placeholderSetVisibility(
            isHidden = false,
            text = getString(R.string.internet_problems),
            imageRes = R.drawable.ic_placeholder_internet,
            textRes = R.string.internet_problems
        )
    }

    private fun showNotFound() {
        placeholderSetVisibility(
            isHidden = false,
            text = "",
            imageRes = R.drawable.ic_placeholder_not_found,
            textRes = R.string.not_found_songs
        )
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
        Log.d("SearchActivity", "historySetVisibility: isVisible = $isVisible")
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

    private fun progressBarSetVisibility(isVisible: Boolean) {
        if (isVisible) progressBar.show() else progressBar.gone()
    }

    private fun openAudioPlayer(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("track", track)
            Log.d("SearchActivity", "Передача трека: ${track.trackName}, previewUrl: ${track.previewUrl}")
            startActivity(intent)
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isAllowed
        if (isAllowed) {
            isAllowed = false
            mainThreadHandler?.postDelayed({isAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        mainThreadHandler?.removeCallbacks(searchRunnable)
        mainThreadHandler?.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

        historyLoad()
    }

    private fun saveSearchState(outState: Bundle) {
        outState.putString(TEXT_AMOUNT, textValue)
        outState.putString("track_list", Gson().toJson(searchList))
        outState.putString("history_list", Gson().toJson(historyTrack))
    }

    private fun restoreSearchState(savedInstanceState: Bundle) {
        Log.d("SearchActivity", "onRestoreInstanceState: вызвано восстановление экрана")

        textValue = savedInstanceState.getString(TEXT_AMOUNT, TEXT_DEF)
        searchEditText.setText(textValue)

        val searchListJson = savedInstanceState.getString("track_list")
        if (!searchListJson.isNullOrEmpty()) {
            searchList.clear()
            searchList.addAll(Gson().fromJson(searchListJson, Array<Track>::class.java))
        }

        historyLoad()
        historySetVisibility(searchEditText.text.isEmpty() && !searchEditText.hasFocus() && historyTrack.isNotEmpty())
        historyAdapter.notifyDataSetChanged()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
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

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val TEXT_DEF = ""
        const val SEARCH_NAME_PREFS = "search_name_prefs"
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 3000L
    }
}