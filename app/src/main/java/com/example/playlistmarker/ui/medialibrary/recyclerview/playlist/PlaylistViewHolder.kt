package com.example.playlistmarker.ui.medialibrary.recyclerview.playlist

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.db.model.Playlist

class PlaylistViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val title: TextView = itemView.findViewById(R.id.playlist_title_rw)
    private val songs: TextView = itemView.findViewById(R.id.counter_songs_playlist_rw)
    private val cover: ImageView = itemView.findViewById(R.id.cover_playlist_rw)
    val rootLayout = itemView.findViewById<ConstraintLayout>(R.id.root_layout)

    fun bind(playlist: Playlist) {
        title.text = playlist.name
        val context = itemView.context
        songs.text = context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.counterTracks,
            playlist.counterTracks
        )
        loadImage(playlist.pathPictureCover)
    }

    private fun loadImage(url: String) {
        val cornerRadius = dpToPx(8f, itemView.context)

        Glide.with(itemView)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(cornerRadius))
            .into(cover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}