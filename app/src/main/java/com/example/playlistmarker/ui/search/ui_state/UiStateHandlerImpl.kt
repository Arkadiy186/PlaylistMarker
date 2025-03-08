    package com.example.playlistmarker.ui.search.ui_state

    import android.os.Looper
    import android.view.View
    import android.widget.ImageView
    import android.widget.LinearLayout
    import android.widget.ProgressBar
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.RecyclerView
    import com.example.playlistmarker.R
    import com.example.playlistmarker.databinding.ActivitySearchBinding
    import com.google.android.material.button.MaterialButton

    class UiStateHandlerImpl(
        private val binding: ActivitySearchBinding,
        private val activity: AppCompatActivity
        ) : UiStateHandler {

        override fun showLoading(isLoading: Boolean) {
            if (Thread.currentThread() != Looper.getMainLooper().thread) {
                activity.runOnUiThread {
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
            val messageResId = activity.getString(message)
            activity.runOnUiThread {
                placeholderSetVisibility(
                    false,
                    messageResId,
                    R.drawable.ic_placeholder_internet,
                    R.string.internet_problems)
            }
        }

        override fun showNotFound() {
            activity.runOnUiThread {
                placeholderSetVisibility(
                    false,
                    "",
                    R.drawable.ic_placeholder_not_found,
                    R.string.not_found_songs
                )
            }
        }

         override fun placeholderSetVisibility(isHidden: Boolean, text: String, imageRes: Int, textRes: Int) {
             activity.runOnUiThread {
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