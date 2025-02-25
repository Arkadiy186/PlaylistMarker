package com.example.playlistmarker.creator

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarker.SearchActivity.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmarker.SearchActivity.Companion.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmarker.data.network.RetrofitClientImpl
import com.example.playlistmarker.data.repository.HistoryRepositoryImpl
import com.example.playlistmarker.data.repository.TrackRepositoryImpl
import com.example.playlistmarker.data.sharedpreferences.HistorySearch
import com.example.playlistmarker.data.utills.NetworkRepositoryImpl
import com.example.playlistmarker.domain.repository.HistoryRepository
import com.example.playlistmarker.domain.repository.TrackRepository
import com.example.playlistmarker.domain.use_case.AudioPlayerInteractor
import com.example.playlistmarker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmarker.domain.use_case.HistoryInteractor
import com.example.playlistmarker.domain.impl.HistoryInteractorImpl
import com.example.playlistmarker.domain.impl.SearchStateInteractorImpl
import com.example.playlistmarker.domain.use_case.TrackInteractor
import com.example.playlistmarker.domain.impl.TrackInteractorImpl
import com.example.playlistmarker.domain.repository.NetworkRepository
import com.example.playlistmarker.domain.use_case.SearchStateInteractor
import com.example.playlistmarker.presentation.presenter.SearchPresenter
import com.example.playlistmarker.presentation.ui_state.UiStateHandler
import com.example.playlistmarker.presentation.ui_state.UiStateHandlerImpl
import com.example.playlistmarker.presentation.utills.DebounceHandler
import com.example.playlistmarker.presentation.utills.DebounceHandlerImpl
import com.example.playlistmarker.presentation.utills.DebounceHelper
import com.example.playlistmarker.presentation.utills.HideKeyboardHelper
import com.example.playlistmarker.presentation.utills.SearchStateManager
import com.google.android.material.button.MaterialButton

@SuppressLint("StaticFieldLeak")
object Creator {

    private var appContext: Context? = null
    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun getContext(): Context {
        return appContext ?: throw IllegalStateException("Context is not initialized!")
    }

    private fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(provideGetTrackRepository())
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(provideGetHistoryRepository(getContext()))
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(mediaPlayer)
    }

    private fun provideNetworkRepository(): NetworkRepository {
        return NetworkRepositoryImpl(getContext())
    }

    fun provideSearchPresenter(placeholder: LinearLayout, placeholderText: TextView, placeholderImage: ImageView, tracksRecyclerView: RecyclerView, placeholderButton: MaterialButton, progressBar: ProgressBar, activity: AppCompatActivity): SearchPresenter {
        return SearchPresenter(provideTrackInteractor(), provideNetworkRepository(), provideHistoryInteractor(), provideUiStateHandler(placeholder, placeholderText, placeholderImage, tracksRecyclerView, placeholderButton, progressBar, activity))
    }

    private fun provideClickDebounceHelper(): DebounceHelper {
        return DebounceHelper(CLICK_DEBOUNCE_DELAY)
    }

    private fun provideSearchDebounceHelper(): DebounceHelper {
        return DebounceHelper(SEARCH_DEBOUNCE_DELAY)
    }

    fun provideDebounceHandler(): DebounceHandler {
        return DebounceHandlerImpl(provideClickDebounceHelper(), provideSearchDebounceHelper())
    }

    private fun provideSearchStateManager(): SearchStateManager {
        return SearchStateManager(getContext().getSharedPreferences("search_name_prefs", Context.MODE_PRIVATE))
    }

    fun provideHideKeyboardHelper(): HideKeyboardHelper {
        return HideKeyboardHelper
    }

    fun provideSearchStateInteractor(): SearchStateInteractor {
        return SearchStateInteractorImpl(provideSearchStateManager())
    }

    fun provideUiStateHandler(placeholder: LinearLayout, placeholderText: TextView, placeholderImage: ImageView, tracksRecyclerView: RecyclerView, placeholderButton: MaterialButton, progressBar: ProgressBar, activity: AppCompatActivity): UiStateHandler {
        return UiStateHandlerImpl(placeholder, tracksRecyclerView, placeholderImage, placeholderText, placeholderButton, progressBar, activity)
    }

    private fun provideGetHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(HistorySearch(context.getSharedPreferences("search_name_prefs", Context.MODE_PRIVATE)))
    }

    private fun provideGetTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitClientImpl())
    }
}