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
import com.example.playlistmarker.ui.searchactivity.SearchActivity.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmarker.ui.searchactivity.SearchActivity.Companion.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmarker.data.network.RetrofitClientImpl
import com.example.playlistmarker.data.repository.HistoryRepositoryImpl
import com.example.playlistmarker.data.repository.ThemeRepositoryImpl
import com.example.playlistmarker.data.repository.TrackRepositoryImpl
import com.example.playlistmarker.data.sharedpreferences.HistorySearch
import com.example.playlistmarker.data.sharedpreferences.ThemePreferences
import com.example.playlistmarker.data.utills.NetworkRepositoryImpl
import com.example.playlistmarker.domain.repository.HistoryRepository
import com.example.playlistmarker.domain.repository.TrackRepository
import com.example.playlistmarker.domain.use_case.AudioPlayerInteractor
import com.example.playlistmarker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmarker.domain.use_case.HistoryInteractor
import com.example.playlistmarker.domain.impl.HistoryInteractorImpl
import com.example.playlistmarker.domain.impl.SearchStateInteractorImpl
import com.example.playlistmarker.domain.impl.ThemeInteractorImpl
import com.example.playlistmarker.domain.use_case.TrackInteractor
import com.example.playlistmarker.domain.impl.TrackInteractorImpl
import com.example.playlistmarker.domain.repository.NetworkRepository
import com.example.playlistmarker.domain.repository.ThemeRepository
import com.example.playlistmarker.domain.use_case.SearchStateInteractor
import com.example.playlistmarker.domain.use_case.ThemeInteractor
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

    //initialization of media player
    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }

    //initialize context
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    //Helper method to get the app context
    fun getContext(): Context {
        return appContext ?: throw IllegalStateException("Context is not initialized!")
    }

    //MARK: - Provide Use Cases (Interactors)
    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(provideHistoryRepository(getContext()))
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(mediaPlayer)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(provideThemeRepository)
    }

    private fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(provideTrackRepository())
    }

    //MARK: - Provide Repositories
    private fun provideNetworkRepository(): NetworkRepository {
        return NetworkRepositoryImpl(getContext())
    }

    private fun provideHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(HistorySearch(context.getSharedPreferences("search_name_prefs", Context.MODE_PRIVATE)))
    }

    private fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitClientImpl())
    }

    private val provideThemeRepository: ThemeRepository by lazy {
        ThemeRepositoryImpl(provideThemePreferences)
    }


    private val provideThemePreferences: ThemePreferences by lazy {
        ThemePreferences(getContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE))
    }

    //MARK - Provide Helpers (Debounce, State, etc.)
    private fun provideClickDebounceHelper(): DebounceHelper {
        return DebounceHelper(CLICK_DEBOUNCE_DELAY)
    }

    private fun provideSearchDebounceHelper(): DebounceHelper {
        return DebounceHelper(SEARCH_DEBOUNCE_DELAY)
    }

    fun provideDebounceHandler(): DebounceHandler {
        return DebounceHandlerImpl(provideClickDebounceHelper(), provideSearchDebounceHelper())
    }

    fun provideHideKeyboardHelper(): HideKeyboardHelper {
        return HideKeyboardHelper
    }

    private fun provideSearchStateManager(): SearchStateManager {
        return SearchStateManager(getContext().getSharedPreferences("search_name_prefs", Context.MODE_PRIVATE))
    }

    fun provideSearchStateInteractor(): SearchStateInteractor {
        return SearchStateInteractorImpl(provideSearchStateManager())
    }

    //MARK: - Provide UI Handlers
    fun provideUiStateHandler(placeholder: LinearLayout, placeholderText: TextView, placeholderImage: ImageView, tracksRecyclerView: RecyclerView, placeholderButton: MaterialButton, progressBar: ProgressBar, activity: AppCompatActivity): UiStateHandler {
        return UiStateHandlerImpl(placeholder, tracksRecyclerView, placeholderImage, placeholderText, placeholderButton, progressBar, activity)
    }

    //MARK: - ProvidePresenter
    fun provideSearchPresenter(placeholder: LinearLayout, placeholderText: TextView, placeholderImage: ImageView, tracksRecyclerView: RecyclerView, placeholderButton: MaterialButton, progressBar: ProgressBar, activity: AppCompatActivity): SearchPresenter {
        return SearchPresenter(provideTrackInteractor(), provideNetworkRepository(), provideHistoryInteractor(), provideUiStateHandler(placeholder, placeholderText, placeholderImage, tracksRecyclerView, placeholderButton, progressBar, activity))
    }
}