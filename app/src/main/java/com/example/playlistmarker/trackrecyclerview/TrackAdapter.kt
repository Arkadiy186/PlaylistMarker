package com.example.playlistmarker.trackrecyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarker.R
import com.example.playlistmarker.domain.model.Track
import com.example.playlistmarker.presentation.model.TrackInfo

class TrackAdapter (private val tracks: ArrayList<TrackInfo>, private val onItemClickListener: (TrackInfo) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track_activity_search, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        Log.d("TrackAdapter", "Binding track: ${track.trackName}")
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onItemClickListener(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}