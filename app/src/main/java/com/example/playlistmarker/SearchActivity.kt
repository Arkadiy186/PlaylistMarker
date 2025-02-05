package com.example.playlistmarker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
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

    private lateinit var backToMainFromSearchActivity : MaterialToolbar
    private lateinit var inputEditText : EditText
    private lateinit var clearButton : ImageButton
    private lateinit var rwTrackList : RecyclerView
    private lateinit var placeholder: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderButton: com.google.android.material.button.MaterialButton
    private lateinit var historySearchText : TextView
    private lateinit var historySearchButton : com.google.android.material.button.MaterialButton
    private lateinit var progressBar: ProgressBar

    private val trackList = ArrayList<Track>()
    private val historyTrack = ArrayList<Track>()
    private var mainThreadHandler : Handler? = null
    private var isAllowed = true
    private val searchRunnable = Runnable { trackSearch(inputEditText.text.toString()) }

    private val searchAdapter = TrackAdapter(trackList) {
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

    private var textValue : String = TEXT_DEF

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_AMOUNT, textValue)
        outState.putString("track_list", Gson().toJson(trackList))
        outState.putString("history_list", Gson().toJson(historyTrack))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("SearchActivity", "onRestoreInstanceState: вызвано восстановление экрана")

        textValue = savedInstanceState.getString(TEXT_AMOUNT, TEXT_DEF)
        inputEditText.setText(textValue)

        val trackListJson = savedInstanceState.getString("track_list")
        if (!trackListJson.isNullOrEmpty()) {
            trackList.clear()
            trackList.addAll(Gson().fromJson(trackListJson, Array<Track>::class.java))
        }

        historyLoad()
        historySetVisibility(inputEditText.text.isEmpty() && !inputEditText.hasFocus() && historyTrack.isNotEmpty())
        historyAdapter.notifyDataSetChanged()
    }

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val TEXT_DEF = ""
        const val SEARCH_NAME_PREFS = "search_name_prefs"
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 3000L
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences(SEARCH_NAME_PREFS, Context.MODE_PRIVATE)
        historySearch = HistorySearch(sharedPreferences)
        mainThreadHandler = Handler(Looper.getMainLooper())

        backToMainFromSearchActivity = findViewById(R.id.activitySearchToolbar)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearButton)
        rwTrackList = findViewById(R.id.recyclerView)
        placeholder = findViewById(R.id.placeholderError)
        placeholderImage = findViewById(R.id.placeholderErrorImage)
        placeholderText = findViewById(R.id.placeholderErrorText)
        placeholderButton = findViewById(R.id.placeholderErrorButton)
        historySearchText = findViewById(R.id.historySearchTextView)
        historySearchButton = findViewById(R.id.historySearchButtonView)
        progressBar = findViewById(R.id.progressBar)


        backToMainFromSearchActivity.setNavigationOnClickListener {
            finish()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            historySetVisibility(hasFocus && inputEditText.text.isEmpty() && historyTrack.isNotEmpty())
        }

        historySearchButton.setOnClickListener {
            historySearch.clearHistory()
            historyTrack.clear()
            historyAdapter.notifyDataSetChanged()
            historySetVisibility(false)
        }

        clearButton.isVisible = !inputEditText.text.isNullOrEmpty()

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            searchAdapter.notifyDataSetChanged()
            hideKeyboard(clearButton)

            if (historyTrack.isNotEmpty()) {
                historySetVisibility(true)
            } else {
                historySetVisibility(false)
            }
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()


                if(inputEditText.hasFocus() && s?.isEmpty() == true && historyTrack.isNotEmpty()) {
                    historySetVisibility(true)
                } else {
                    searchDebounce()
                    historySetVisibility(false)
                }

                if (s.isNullOrEmpty()) {
                    placeholderSetVisibility(true)
                }

                historyLoad()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        rwTrackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rwTrackList.adapter = searchAdapter

        // отключил DONE на клавиатуре
        /*inputEditText.setOnEditorActionListener { _, actionID, _ ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    trackSearch(inputEditText.text.toString())
                }
                inputEditText.clearFocus()
            }
            false
        }*/

        placeholderButton.setOnClickListener {
            val query = inputEditText.text.toString()
            if (isInternetAvailable()) {
                if (query.isEmpty()) {
                    if (trackList.isNotEmpty()) {
                        showListWithoutPlaceholder()
                    } else {
                        showListWithoutPlaceholder()
                    }
                } else {
                    trackSearch(query)
                }
            } else {
                if (query.isEmpty()) {
                    showErrorInternet()
                } else {
                    showErrorInternet()
                }
            }
        }
        historyLoad()
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
        if (inputEditText.text.isNotEmpty()) {
            progressBarSetVisibility(true)
            historySetVisibility(false)
            RetrofitApiService.itunesApiService.search(inputSong).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    progressBarSetVisibility(false)
                    if (response.code() == 200) {
                        trackList.clear()
                        showListWithoutPlaceholder()

                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
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
        showPlaceholder(text = getString(R.string.internet_problems),
            lightImageRes = R.drawable.no_internet,
            darkImageRes = R.drawable.no_internet_dark,
            textRes = R.string.internet_problems
        )
    }

    private fun showNotFound() {
        showPlaceholder(
            text = "",
            lightImageRes = R.drawable.not_found,
            darkImageRes = R.drawable.not_found_dark,
            textRes = R.string.not_found_songs
        )
    }

    private fun showListWithoutPlaceholder() {
        placeholderSetVisibility (isHidden = true)
    }

    private fun showPlaceholder(text: String, lightImageRes: Int, darkImageRes: Int, textRes: Int) {
        placeholderSetVisibility(
            isHidden = false,
            text = text,
            lightImageRes = lightImageRes,
            darkImageRes = darkImageRes,
            textRes = textRes
        )
    }

    private fun placeholderSetVisibility(isHidden: Boolean, text: String = "", lightImageRes: Int = 0, darkImageRes: Int = 0, textRes: Int = 0) {
        if (isHidden) {
            placeholder.visibility = View.GONE
            rwTrackList.visibility = View.VISIBLE
        } else {
            placeholder.visibility = View.VISIBLE
            rwTrackList.visibility = View.GONE
            placeholderButton.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            placeholderImage.setImageResource(if (isDark()) darkImageRes else lightImageRes)
            placeholderText.setText(textRes)
        }
    }

    private fun historySetVisibility(isVisible: Boolean) {
        Log.d("SearchActivity", "historySetVisibility: isVisible = $isVisible")
        if (isVisible) {
            rwTrackList.adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
            historySearchText.visibility = View.VISIBLE
            rwTrackList.visibility = View.VISIBLE
            historySearchButton.visibility = View.VISIBLE
        } else {
            rwTrackList.adapter = searchAdapter
            searchAdapter.notifyDataSetChanged()
            historySearchText.visibility = View.GONE
            rwTrackList.visibility = View.GONE
            historySearchButton.visibility = View.GONE
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

    private fun isDark(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
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
}