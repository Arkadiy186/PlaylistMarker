package com.example.playlistmarker.ui.search.utills.debounce

import kotlinx.coroutines.CoroutineScope

interface DebounceHandler {
    fun <T> debounce(delayMillis: Long, coroutineScope: CoroutineScope, useLastParam: Boolean, action: (T) -> Unit): (T) -> Unit
}