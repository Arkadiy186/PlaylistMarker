package com.example.playlistmarker.creator

import android.annotation.SuppressLint
import android.content.Context
import com.example.playlistmarker.data.player.impl.PositionTimeRepositoryImpl
import com.example.playlistmarker.data.player.sharedpreferences.PositionTime
import com.example.playlistmarker.ui.search.activity.SearchActivity.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmarker.ui.search.activity.SearchActivity.Companion.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmarker.data.search.network.RetrofitClientImpl
import com.example.playlistmarker.data.search.impl.HistoryRepositoryImpl
import com.example.playlistmarker.data.settings.impl.ThemeRepositoryImpl
import com.example.playlistmarker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmarker.data.search.sharedpreferences.HistorySearch
import com.example.playlistmarker.data.settings.sharedpreferences.ThemePreferences
import com.example.playlistmarker.data.search.impl.utills.NetworkRepositoryImpl
import com.example.playlistmarker.domain.player.impl.PositionTimeInteractorImpl
import com.example.playlistmarker.domain.player.repository.PositionTimeRepository
import com.example.playlistmarker.domain.search.repository.HistoryRepository
import com.example.playlistmarker.domain.search.repository.TrackRepository
import com.example.playlistmarker.domain.player.use_cases.AudioPlayerInteractor
import com.example.playlistmarker.domain.player.use_cases.PositionTimeInteractor
import com.example.playlistmarker.ui.audioplayer.impl.AudioPlayerInteractorImpl
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.domain.search.impl.HistoryInteractorImpl
import com.example.playlistmarker.domain.search.impl.NetworkInteractorImpl
import com.example.playlistmarker.domain.search.impl.SearchStateInteractorImpl
import com.example.playlistmarker.domain.settings.impl.ThemeInteractorImpl
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
import com.example.playlistmarker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmarker.domain.search.repository.utills.NetworkRepository
import com.example.playlistmarker.domain.search.use_cases.NetworkInteractor
import com.example.playlistmarker.domain.settings.repository.ThemeRepository
import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandler
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandlerImpl
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHelper
import com.example.playlistmarker.ui.search.utills.hidekeyboard.HideKeyboardHelper
import com.example.playlistmarker.ui.search.utills.statemanager.SearchStateManager

@SuppressLint("StaticFieldLeak")
object Creator {

    private var appContext: Context? = null

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
        return AudioPlayerInteractorImpl()
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(provideTrackRepository())
    }

    fun provideNetworkInteractor(): NetworkInteractor {
        return NetworkInteractorImpl(provideNetworkRepository())
    }

    fun providePositionTimeInteractor(): PositionTimeInteractor {
        return PositionTimeInteractorImpl(providePositionTimeRepository)
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

    private val providePositionTimeRepository: PositionTimeRepository by lazy {
        PositionTimeRepositoryImpl(PositionTime(getContext().getSharedPreferences("player_position_prefs", Context.MODE_PRIVATE)))
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
}