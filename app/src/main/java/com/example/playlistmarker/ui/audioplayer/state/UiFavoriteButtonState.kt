package com.example.playlistmarker.ui.audioplayer.state

import com.example.playlistmarker.R

sealed class UiFavoriteButtonState(val isFavourite: Boolean, val resId: Int) {
    class IsFavourite : UiFavoriteButtonState(true, R.drawable.button_like_true)
    class NotFavourite : UiFavoriteButtonState(false, R.drawable.button_like_false)
}