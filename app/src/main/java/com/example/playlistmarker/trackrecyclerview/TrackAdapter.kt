package com.example.playlistmarker.trackrecyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarker.R

class TrackAdapter (private val tracks: ArrayList<Track>, private val onItemClickListener: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {
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