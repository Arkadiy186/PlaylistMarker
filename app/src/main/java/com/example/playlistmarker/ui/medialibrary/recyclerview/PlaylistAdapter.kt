package com.example.playlistmarker.ui.medialibrary.recyclerview

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.db.model.Playlist

class PlaylistAdapter(private val playlist: List<Playlist>, private val onItemClickListener: (Playlist) -> Unit): RecyclerView.Adapter<PlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val layoutParams = holder.rootLayout.layoutParams as ViewGroup.MarginLayoutParams

        val marginSide = dpToPx(16f, holder.itemView.context)
        val marginBetween = dpToPx(8f, holder.itemView.context)

        if (position % 2 == 0) {
            layoutParams.marginEnd = marginBetween / 2
            layoutParams.marginStart = marginSide
        } else {
            layoutParams.marginStart = marginBetween / 2
            layoutParams.marginEnd = marginSide
        }

        val playlist = playlist[position]
        holder.bind(playlist)

        holder.itemView.setOnClickListener {
            onItemClickListener(playlist)
        }
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}