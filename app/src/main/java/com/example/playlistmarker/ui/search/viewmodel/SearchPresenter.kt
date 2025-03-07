package com.example.playlistmarker.ui.search.viewmodel

import com.example.playlistmarker.R
import com.example.playlistmarker.domain.search.model.Track
import com.example.playlistmarker.domain.search.repository.utills.NetworkRepository
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.domain.search.use_cases.TrackInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.search.ui_state.UiStateHandler

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
                    val trackInfoDetails = tracks.map { track ->
                        TrackInfoDetailsMapper.map(track)
                    }
                    view?.showTracks(trackInfoDetails)
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
            val trackInfo = history.map { TrackInfoDetailsMapper.map(it) }
            view?.showHistory(trackInfo)
        }
    }
}