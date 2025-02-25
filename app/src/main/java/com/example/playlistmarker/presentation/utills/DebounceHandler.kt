package com.example.playlistmarker.presentation.utills

interface DebounceHandler {
    fun handleSearchDebounce(query: String, action: (String) -> Unit)
    fun handleClickDebounce(action: () -> Boolean): Boolean
}