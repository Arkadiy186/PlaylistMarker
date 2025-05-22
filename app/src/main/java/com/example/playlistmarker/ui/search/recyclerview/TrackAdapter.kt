package com.example.playlistmarker.ui.search.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarker.R
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

class TrackAdapter (private val tracks: MutableList<TrackInfoDetails>, private val onItemClickListener: (TrackInfoDetails) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track_activity_search, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onItemClickListener(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}