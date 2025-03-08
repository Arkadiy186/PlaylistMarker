package com.example.playlistmarker.domain.search.impl

import com.example.playlistmarker.domain.search.repository.utills.NetworkRepository
import com.example.playlistmarker.domain.search.use_cases.NetworkInteractor

class NetworkInteractorImpl(private val networkRepository: NetworkRepository) : NetworkInteractor {
    override fun isInternetAvailable(): Boolean {
        return networkRepository.isInternetAvailable()
    }
}