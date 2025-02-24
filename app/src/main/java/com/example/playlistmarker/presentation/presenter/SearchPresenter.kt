package com.example.playlistmarker.presentation.presenter

import com.example.playlistmarker.data.utills.NetworkHelper
import com.example.playlistmarker.domain.model.Track
import com.example.playlistmarker.domain.use_case.HistoryInteractor
import com.example.playlistmarker.domain.use_case.TrackInteractor
import com.example.playlistmarker.presentation.mapper.TrackMapper
import com.example.playlistmarker.presentation.view.SearchView

class SearchPresenter(private val trackInteractor: TrackInteractor, private val networkHelper: NetworkHelper, private val historyInteractor: HistoryInteractor) {
    private var view: SearchView? = null

    fun attachView(view: SearchView) {
        this.view = view
    }

    fun detachView(view: SearchView) {
        this.view = view
    }

    fun searchTrack(query: String) {
        if (query.isEmpty()) return

        if (!networkHelper.isInternetAvailable()) {
            view?.showErrorInternet()
            return
        }

        view?.showLoading(true)

        trackInteractor.searchTrack(query, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>) {
                view?.showLoading(false)

                if (foundTracks.isEmpty()) {
                    view?.showNotFound()
                } else {
                    val trackInfo = foundTracks.map { TrackMapper.map(it) }
                    view?.showTracks(trackInfo)
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