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
    import com.google.android.material.button.MaterialButton

    class UiStateHandlerImpl(
        private val placeholder: LinearLayout,
        private val tracksRecyclerView: RecyclerView,
        private val placeholderImage: ImageView,
        private val placeholderText: TextView,
        private val placeholderButton: MaterialButton,
        private val progressBar: ProgressBar,
        private val activity: AppCompatActivity
        ) : UiStateHandler {

        override fun showLoading(isLoading: Boolean) {
            if (Thread.currentThread() != Looper.getMainLooper().thread) {
                activity.runOnUiThread {
                    if (isLoading) {
                        progressBar.show()
                        tracksRecyclerView.gone()
                    } else {
                        progressBar.gone()
                        tracksRecyclerView.show()
                    }
                }
            } else {
                if (isLoading) {
                    progressBar.show()
                    tracksRecyclerView.gone()
                } else {
                    progressBar.gone()
                    tracksRecyclerView.show()
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
                     placeholder.gone()
                     tracksRecyclerView.show()
                 } else {
                     placeholder.show()
                     tracksRecyclerView.gone()
                     placeholderImage.setImageResource(imageRes)
                     placeholderText.setText(textRes)
                     if (text.isEmpty()) {
                         placeholderButton.gone()
                     } else {
                         placeholderButton.show()
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