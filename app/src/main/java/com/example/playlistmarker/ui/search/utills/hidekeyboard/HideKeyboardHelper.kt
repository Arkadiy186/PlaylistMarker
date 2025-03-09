package com.example.playlistmarker.ui.search.utills.hidekeyboard

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.playlistmarker.creator.Creator

object HideKeyboardHelper {
    fun hideKeyboard(view: View) {
        val context = Creator.getContext()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}