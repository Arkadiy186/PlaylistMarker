package com.example.playlistmarker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarker.trackAPI.TrackAPI
import com.example.playlistmarker.trackrecyclerview.Track
import com.example.playlistmarker.trackrecyclerview.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText : EditText
    private lateinit var clearButton : ImageButton
    private lateinit var rwTrackList : RecyclerView

    private val trackList = ArrayList<Track>()
    private val adapter = TrackAdapter(trackList)
    private var textValue : String = TEXT_DEF

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_AMOUNT, textValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textValue = savedInstanceState.getString(TEXT_AMOUNT, TEXT_DEF)
        findViewById<EditText>(R.id.inputEditText).setText(textValue)
    }

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val TEXT_DEF = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backToMainFromSearchActivity = findViewById<MaterialToolbar>(R.id.activitySearchToolbar)
        backToMainFromSearchActivity.setNavigationOnClickListener {
            finish()
        }

        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearButton)
        rwTrackList = findViewById(R.id.recyclerView)

        clearButton.isVisible = !inputEditText.text.isNullOrEmpty()

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(clearButton)
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        rwTrackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rwTrackList.adapter = adapter



    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
