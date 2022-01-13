package com.micudasoftware.gpstracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.micudasoftware.gpstracker.databinding.ItemTrackBinding
import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.other.Utils
import java.text.SimpleDateFormat
import java.util.*

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.hashCode() == oldItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Track>) = differ.submitList(list)

    private lateinit var binding: ItemTrackBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        binding = ItemTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this)
                .load(track.image)
                .apply(RequestOptions().override(800,400))
                .centerCrop()
                .into(binding.ivTrackImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = track.dateInMillis
            }
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${track.avgSpeedInKMH}km/h"
            binding.tvAvgSpeed.text = avgSpeed

            val distanceInKMH = "${track.distanceInMeters / 1000f}km"
            binding.tvDistance.text = distanceInKMH

            binding.tvTime.text = Utils.getFormattedTime(track.timeInMillis)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}