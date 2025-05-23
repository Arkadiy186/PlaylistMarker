package com.example.playlistmarker.ui.medialibrary.handler

import android.view.View
import androidx.fragment.app.Fragment
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.FragmentFavouriteTracksBinding

class UiStateFavouriteHandlerImpl(
    private val binding: FragmentFavouriteTracksBinding,
    private val fragment: Fragment
) : UiStateFavouriteHandler {
    override fun favouriteNotFound() {
        fragment.requireActivity().runOnUiThread {
            placeholderSetVisibility(
                false,
                "",
                R.drawable.ic_placeholder_not_found,
                R.string.media_library_empty
            )
        }
    }

    override fun placeholderSetVisibility(
        isHidden: Boolean,
        text: String,
        imageRes: Int,
        textRes: Int
    ) {
        if (isHidden) {
            binding.recyclerView.show()
            binding.placeholderError.gone()
        } else {
            binding.recyclerView.gone()
            binding.placeholderError.show()
        }
    }

    private fun View.show() {
        visibility = View.VISIBLE
    }

    private fun View.gone() {
        visibility = View.GONE
    }
}