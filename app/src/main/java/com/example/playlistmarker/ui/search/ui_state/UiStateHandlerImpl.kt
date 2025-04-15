    package com.example.playlistmarker.ui.search.ui_state

    import android.os.Looper
    import android.view.View
    import androidx.fragment.app.Fragment
    import com.example.playlistmarker.R
    import com.example.playlistmarker.databinding.FragmentSearchBinding

    class UiStateHandlerImpl(
        private val binding: FragmentSearchBinding,
        private val fragment: Fragment
        ) : UiStateHandler {

        override fun showLoading(isLoading: Boolean) {
            if (Thread.currentThread() != Looper.getMainLooper().thread) {
                fragment.requireActivity().runOnUiThread {
                    if (isLoading) {
                        binding.progressBar.show()
                        binding.recyclerView.gone()
                    } else {
                        binding.progressBar.gone()
                        binding.recyclerView.show()
                    }
                }
            } else {
                if (isLoading) {
                    binding.progressBar.show()
                    binding.recyclerView.gone()
                } else {
                    binding.progressBar.gone()
                    binding.recyclerView.show()
                }
            }
        }

        override fun showErrorInternet(message: Int) {
            val messageResId = fragment.getString(message)
            fragment.requireActivity().runOnUiThread {
                placeholderSetVisibility(
                    false,
                    messageResId,
                    R.drawable.ic_placeholder_internet,
                    R.string.internet_problems)
            }
        }

        override fun showNotFound() {
            fragment.requireActivity().runOnUiThread {
                placeholderSetVisibility(
                    false,
                    "",
                    R.drawable.ic_placeholder_not_found,
                    R.string.not_found_songs
                )
            }
        }

         override fun placeholderSetVisibility(isHidden: Boolean, text: String, imageRes: Int, textRes: Int) {
             fragment.requireActivity().runOnUiThread {
                 if (isHidden) {
                     binding.placeholderError.gone()
                     binding.recyclerView.show()
                 } else {
                     binding.historySearchButtonView.gone()
                     binding.historySearchTextView.gone()
                     binding.placeholderError.show()
                     binding.recyclerView.gone()
                     binding.placeholderErrorImage.setImageResource(imageRes)
                     binding.placeholderErrorText.setText(textRes)
                     if (text.isEmpty()) {
                         binding.placeholderErrorButton.gone()
                     } else {
                         binding.placeholderErrorButton.show()
                     }
                 }
             }
        }

        private fun View.show() {
            visibility = View.VISIBLE
        }

        private fun View.gone() {
            visibility = View.GONE
        }
    }