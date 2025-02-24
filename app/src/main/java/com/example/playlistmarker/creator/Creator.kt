package com.example.playlistmarker.creator

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmarker.SearchActivity.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmarker.SearchActivity.Companion.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmarker.data.network.RetrofitClientImpl
import com.example.playlistmarker.data.repository.HistoryRepositoryImpl
import com.example.playlistmarker.data.repository.TrackRepositoryImpl
import com.example.playlistmarker.data.sharedpreferences.HistorySearch
import com.example.playlistmarker.data.utills.NetworkHelper
import com.example.playlistmarker.domain.repository.HistoryRepository
import com.example.playlistmarker.domain.repository.TrackRepository
import com.example.playlistmarker.domain.use_case.AudioPlayerInteractor
import com.example.playlistmarker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmarker.domain.use_case.HistoryInteractor
import com.example.playlistmarker.domain.impl.HistoryInteractorImpl
import com.example.playlistmarker.domain.use_case.TrackInteractor
import com.example.playlistmarker.domain.impl.TrackInteractorImpl
import com.example.playlistmarker.presentation.presenter.SearchPresenter
import com.example.playlistmarker.presentation.utills.DebounceHelper
import com.example.playlistmarker.presentation.utills.HideKeyboardHelper
import com.example.playlistmarker.presentation.utills.SearchStateManager

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

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(provideGetTrackRepository())
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(provideGetHistoryRepository(getContext()))
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(mediaPlayer)
    }

    fun provideNetworkHelper(): NetworkHelper {
        return NetworkHelper(getContext())
    }

    fun provideSearchPresenter(): SearchPresenter {
        return SearchPresenter(provideTrackInteractor(), provideNetworkHelper(), provideHistoryInteractor())
    }

    fun provideClickDebounceHelper(): DebounceHelper {
        return DebounceHelper(CLICK_DEBOUNCE_DELAY)
    }

    fun provideSearchDebounceHelper(): DebounceHelper {
        return DebounceHelper(SEARCH_DEBOUNCE_DELAY)
    }

    fun provideSearchStateManager(): SearchStateManager {
        return SearchStateManager(getContext().getSharedPreferences("search_name_prefs", Context.MODE_PRIVATE))
    }

    fun provideHideKeyboardHelper(): HideKeyboardHelper {
        return HideKeyboardHelper
    }

    private fun provideGetHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(HistorySearch(context.getSharedPreferences("search_name_prefs", Context.MODE_PRIVATE)))
    }

    private fun provideGetTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitClientImpl())
    }
}