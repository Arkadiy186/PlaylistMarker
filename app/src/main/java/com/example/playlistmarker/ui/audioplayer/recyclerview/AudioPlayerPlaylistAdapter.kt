package com.example.playlistmarker.ui.audioplayer.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.db.model.Playlist

class AudioPlayerPlaylistAdapter(
    private val playlist: List<Playlist>,
    private val onItemClickListener: (Playlist) -> Unit
): RecyclerView.Adapter<AudioPlayerPlaylistViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioPlayerPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_in_audioplayer, parent, false)
        return AudioPlayerPlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioPlayerPlaylistViewHolder, position: Int) {
        val playlist = playlist[position]
        holder.bind(playlist)

        holder.itemView.setOnClickListener {
            onItemClickListener(playlist)
        }
    }

    override fun getItemCount(): Int {
        return playlist.size
    }
}