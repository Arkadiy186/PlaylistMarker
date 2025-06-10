package com.example.playlistmarker.ui.audioplayer.recyclerview

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.db.model.Playlist

class AudioPlayerPlaylistViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val title: TextView = itemView.findViewById(R.id.playlist_audio_player_name)
    private val songs: TextView = itemView.findViewById(R.id.playlist_audio_player_counter_tracks)
    private val cover: ImageView = itemView.findViewById(R.id.playlist_cover_playlist_audio_player)

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
        val cornerRadius = dpToPx(2f, itemView.context)

        Glide.with(itemView)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .into(cover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}