package com.example.playlistmarker.ui.search.utills

interface DebounceHandler {
    fun handleSearchDebounce(query: String, action: (String) -> Unit)
    fun handleClickDebounce(action: () -> Boolean): Boolean
}