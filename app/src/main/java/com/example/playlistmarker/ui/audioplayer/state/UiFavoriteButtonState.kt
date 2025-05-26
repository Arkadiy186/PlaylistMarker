package com.example.playlistmarker.ui.audioplayer.state

import com.example.playlistmarker.R

sealed class UiFavoriteButtonState(val isFavourite: Boolean, val resId: Int) {
    class IsFavourite : UiFavoriteButtonState(true, R.drawable.ic_like_favourite)
    class NotFavourite : UiFavoriteButtonState(false, R.drawable.ic_like_not_favourite)
}