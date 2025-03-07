package com.example.playlistmarker.ui.search.utills

class DebounceHandlerImpl(
    private val clickDebounceHelper: DebounceHelper,
    private val searchDebounceHelper: DebounceHelper
) : DebounceHandler {
    override fun handleClickDebounce(action: () -> Boolean): Boolean {
        clickDebounceHelper.clickDebounceRun()
        return action()
    }

    override fun handleSearchDebounce(query: String, action: (String) -> Unit) {
        searchDebounceHelper.searchDebounceRun { action(query) }
    }
}