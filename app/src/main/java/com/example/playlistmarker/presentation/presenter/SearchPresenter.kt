package com.example.playlistmarker.presentation.presenter

import com.example.playlistmarker.R
import com.example.playlistmarker.domain.model.Track
import com.example.playlistmarker.domain.repository.NetworkRepository
import com.example.playlistmarker.domain.use_case.HistoryInteractor
import com.example.playlistmarker.domain.use_case.TrackInteractor
import com.example.playlistmarker.presentation.mapper.TrackMapper
import com.example.playlistmarker.presentation.ui_state.UiStateHandler
import com.example.playlistmarker.presentation.view.SearchView

class SearchPresenter(
    private val trackInteractor: TrackInteractor,
    private val networkRepository: NetworkRepository,
    private val historyInteractor: HistoryInteractor,
    private val uiStateHandler: UiStateHandler,
) {
    private var view: SearchView? = null

    fun attachView(view: SearchView) {
        this.view = view
    }

    fun detachView(view: SearchView) {
        this.view = view
    }

    fun searchTrack(query: String) {
        if (query.isEmpty()) return

        if (!networkRepository.isInternetAvailable()) {
            uiStateHandler.showErrorInternet(R.string.internet_problems)
            return
        }

        uiStateHandler.placeholderSetVisibility(isHidden = true)
        uiStateHandler.showLoading(true)

        trackInteractor.searchTrack(query, object : TrackInteractor.TrackConsumer {
            override fun onTrackFound(tracks: List<Track>) {
                uiStateHandler.showLoading(false)

                if (tracks.isEmpty()) {
                    uiStateHandler.showNotFound()
                } else {
                    val trackInfo = tracks.map { TrackMapper.map(it) }
                    view?.showTracks(trackInfo)
                }
            }

            override fun onError(error: Throwable) {
                uiStateHandler.showLoading(false)
                if (!networkRepository.isInternetAvailable()) {
                    uiStateHandler.showErrorInternet(R.string.internet_problems)
                }else {
                    uiStateHandler.showNotFound()
                }
            }
        })
    }

    fun loadHistory() {
        historyInteractor.loadHistory {history ->
            val trackInfo = history.map { TrackMapper.map(it) }
            view?.showHistory(trackInfo)
        }
    }
}