package com.example.playlistmarker.ui.medialibrary.handler.playlists

import android.view.View
import androidx.fragment.app.Fragment
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.FragmentPlaylistsBinding

class UiStatePlaylistsHandlerImpl(
    private val binding: FragmentPlaylistsBinding,
    private val fragment: Fragment
) : UiStatePlaylistHandler {
    override fun playlistsIsEmpty() {
        fragment.requireActivity().runOnUiThread {
            setPlaceholderVisibility(
                false,
                "",
                R.drawable.ic_placeholder_not_found,
                R.string.not_created_playlist
            )
        }
    }

    override fun setPlaceholderVisibility(
        isHidden: Boolean,
        text: String,
        imageRes: Int,
        textRes: Int
    ) {
        if (isHidden) {
            binding.playlistContent.show()
            binding.placeholderLayout.gone()
        } else {
            binding.playlistContent.gone()
            binding.placeholderLayout.show()
        }
    }

    private fun View.show() {
        visibility = View.VISIBLE
    }

    private fun View.gone() {
        visibility = View.GONE
    }
}