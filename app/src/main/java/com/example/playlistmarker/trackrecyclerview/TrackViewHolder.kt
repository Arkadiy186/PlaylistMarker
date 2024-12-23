package com.example.playlistmarker.trackrecyclerview

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarker.R
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val itemTrackName : TextView = itemView.findViewById(R.id.trackNameItem)
    private val itemTrackArtistAndTime : TextView = itemView.findViewById(R.id.trackArtistAndTimeItem)
    private val itemImageViewTrack : ImageView = itemView.findViewById(R.id.imageViewItemTrack)

    fun bind(model : Track) {
        itemTrackName.text = model.trackName
        itemTrackArtistAndTime.text = "${model.artistName} • ${SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime)}"

        val cornerRadius = dpToPx(2f, itemView.context)

        if (!model.artworkUrl100.isNullOrEmpty()) {
            Glide.with(itemView)
                .load(model.artworkUrl100)
                .fitCenter()
                .placeholder(R.drawable.ic_placeholder)
                .transform(RoundedCorners(cornerRadius))
                .into(itemImageViewTrack)

        } else {
            Glide.with(itemView)
                .load(R.drawable.ic_placeholder)
                .transform(RoundedCorners(cornerRadius))
                .into(itemImageViewTrack)
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}