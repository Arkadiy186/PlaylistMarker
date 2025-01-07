package com.example.playlistmarker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

    private lateinit var backToMainFromSearchActivity : MaterialToolbar
    private lateinit var inputEditText : EditText
    private lateinit var clearButton : ImageButton
    private lateinit var rwTrackList : RecyclerView
    private lateinit var placeholder: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderButton: com.google.android.material.button.MaterialButton
    private lateinit var historySearchView : LinearLayout
    private lateinit var historySearchText : TextView
    private lateinit var historySearchRW : RecyclerView
    private lateinit var historySearchButton : com.google.android.material.button.MaterialButton

    private val trackList = ArrayList<Track>()
    private val historyTrack = ArrayList<Track>()

    private val searchAdapter = TrackAdapter(trackList) {
        historySearch.addTrackHistory(it)
    }

    private val historyAdapter = TrackAdapter(historyTrack) {
        Toast.makeText(applicationContext, "Player will be soon", Toast.LENGTH_SHORT).show()
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
        textValue = savedInstanceState.getString(TEXT_AMOUNT, TEXT_DEF)
        findViewById<EditText>(R.id.inputEditText).setText(textValue)

        val trackListJson = savedInstanceState.getString("track_list")
        val historyListJson = savedInstanceState.getString("history_key")
        if (!trackListJson.isNullOrEmpty()) {
            trackList.clear()
            trackList.addAll(Gson().fromJson(trackListJson, Array<Track>::class.java))
        }
        if (!historyListJson.isNullOrEmpty()) {
            historyTrack.clear()
            historyTrack.addAll(Gson().fromJson(historyListJson, Array<Track>::class.java))
        }
        searchAdapter.notifyDataSetChanged()
        historyAdapter.notifyDataSetChanged()
    }

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val TEXT_DEF = ""
        const val SEARCH_NAME_PREFS = "search_name_prefs"
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences(SEARCH_NAME_PREFS, Context.MODE_PRIVATE)
        historySearch = HistorySearch(sharedPreferences)

        backToMainFromSearchActivity = findViewById(R.id.activitySearchToolbar)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearButton)
        rwTrackList = findViewById(R.id.recyclerView)
        placeholder = findViewById(R.id.placeholderError)
        placeholderImage = findViewById(R.id.placeholderErrorImage)
        placeholderText = findViewById(R.id.placeholderErrorText)
        placeholderButton = findViewById(R.id.placeholderErrorButton)
        historySearchView = findViewById(R.id.historySearchView)
        historySearchText = findViewById(R.id.historySearchTextView)
        historySearchRW = findViewById(R.id.historySearchRecycler)
        historySearchButton = findViewById(R.id.historySearchButton)


        backToMainFromSearchActivity.setNavigationOnClickListener {
            finish()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (inputEditText.text.isEmpty() && historyTrack.isNotEmpty()) {
                    historySearchView.visibility = View.VISIBLE
                    rwTrackList.visibility = View.GONE
                } else {
                    showListWithoutPlaceholder()
                }
            } else {
                if (historyTrack.isNotEmpty()) {
                    historySearchView.visibility = View.VISIBLE
                    placeholder.visibility = View.GONE
                    rwTrackList.visibility = View.GONE
                } else {
                    placeholderSetVisibility(false, "", R.drawable.not_found, R.drawable.not_found_dark, R.string.not_found_songs)
                }
            }
        }

        clearButton.isVisible = !inputEditText.text.isNullOrEmpty()

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            searchAdapter.notifyDataSetChanged()
            hideKeyboard(clearButton)
            inputEditText.clearFocus()

            if (historyTrack.isNotEmpty()) {
                historyLoad()
                rwTrackList.visibility = View.GONE
                historySearchView.visibility = View.VISIBLE
            } else {
                showListWithoutPlaceholder()
            }
        }

        historySearchButton.setOnClickListener {
            historySearch.clearHistory()
            historyTrack.clear()
            historyAdapter.notifyDataSetChanged()
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                if (s.isNullOrEmpty()) {
                    if (historyTrack.isNotEmpty()) {
                        historySearchView.visibility = View.VISIBLE
                        rwTrackList.visibility = View.GONE
                        placeholder.visibility = View.GONE
                    } else {
                        if (inputEditText.hasFocus()) {
                            showListWithoutPlaceholder()
                        } else {
                            placeholderSetVisibility(false, "", R.drawable.not_found, R.drawable.not_found_dark, R.string.not_found_songs)
                        }
                    }
                } else {
                    historySearchView.visibility = View.GONE
                    rwTrackList.visibility = View.VISIBLE
                    placeholder.visibility = View.GONE
                }
                historyLoad()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        rwTrackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rwTrackList.adapter = searchAdapter

        historySearchRW.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historySearchRW.adapter = historyAdapter

        inputEditText.setOnEditorActionListener { _, actionID, _ ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {
                trackSearch(inputEditText.text.toString())
                inputEditText.clearFocus()
            }
            false
        }

        placeholderButton.setOnClickListener {
            val query = inputEditText.text.toString()
            if (isInternetAvailable() == true) {
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
    }

    @SuppressLint("NotifyDataSetChanged")
    fun historyLoad() {
        historyTrack.clear()
        historyTrack.addAll(historySearch.getHistory())
        historyAdapter.notifyDataSetChanged()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilites = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilites?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun trackSearch(inputSong: String) {
        RetrofitApiService.itunesApiService.search(inputSong).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.code() == 200) {
                    trackList.clear()
                    showListWithoutPlaceholder()


                    if (response.body()?.results?.isNotEmpty() == true) {
                        trackList.addAll(response.body()?.results!!)
                        searchAdapter.notifyDataSetChanged()
                    } else {
                        placeholderSetVisibility(false, "", R.drawable.not_found, R.drawable.not_found_dark, R.string.not_found_songs)
                    }
                } else {
                    showErrorInternet()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showErrorInternet()
            }
        })
    }

    private fun showErrorInternet() {
        placeholderSetVisibility(false, getString(R.string.internet_problems), R.drawable.no_internet, R.drawable.no_internet_dark,R.string.internet_problems)
    }

    private fun showListWithoutPlaceholder() {
        placeholderSetVisibility(true, "", R.drawable.not_found, R.drawable.not_found_dark, R.string.not_found_songs)
    }

    private fun placeholderSetVisibility(isNotVisible: Boolean, text: String, lightImageRes: Int, darkImageRes: Int, textRes: Int) {
        val imageRes = if (isDark()) darkImageRes else lightImageRes
        if(isNotVisible) {
            placeholder.visibility = View.GONE
            rwTrackList.visibility = View.VISIBLE
            historySearchView.visibility = View.GONE
        }else {
            trackList.clear()
            placeholder.visibility = View.VISIBLE
            rwTrackList.visibility = View.GONE
            historySearchView.visibility = View.GONE
            placeholderButton.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            placeholderImage.setImageResource(imageRes)
            placeholderText.setText(textRes)
        }
    }

    private fun isDark(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}