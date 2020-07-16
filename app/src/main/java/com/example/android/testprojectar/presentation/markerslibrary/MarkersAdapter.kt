package com.example.android.testprojectar.presentation.markerslibrary

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.testprojectar.listeners.OnMarkerClickListener
import com.example.android.testprojectar.R
import com.example.android.testprojectar.models.MarkerModel
import kotlinx.android.synthetic.main.item_marker.view.*
import java.io.File


class MarkersAdapter(
    private val markers: List<MarkerModel>,
    val markerClickListener: OnMarkerClickListener
) : RecyclerView.Adapter<MarkersAdapter.MarkerVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_marker, parent, false)
        return MarkerVH(view)
    }

    override fun getItemCount(): Int {
        return markers.size
    }

    override fun onBindViewHolder(holder: MarkerVH, position: Int) {
        holder.bind(markers[position])
    }

    inner class MarkerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(marker: MarkerModel) {
            itemView.tvMarkerName.text = marker.name
            Glide.with(itemView.context)
                .load(Uri.fromFile(File(marker.imagePath)))
                .into(itemView.ivMarkerPreview)

            itemView.ivEdit.setOnClickListener {
                markerClickListener.clickOnEditMarker(
                    marker,
                    adapterPosition
                )
            }
            itemView.ivRemove.setOnClickListener {
                markerClickListener.clickOnRemoveMarker(
                    marker,
                    adapterPosition
                )
            }
        }
    }
}