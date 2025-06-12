package com.example.movieland.ui.features.home.regionanddistrict

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.data.firebase.model.region.FirestoreRegion
import com.example.movieland.databinding.ItemRegionBinding

class RegionAdapter(
    private val onItemClick: (FirestoreRegion) -> Unit
) : RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {

    private var regionList = listOf<FirestoreRegion>()

    fun submitList(data: List<FirestoreRegion>) {
        regionList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegionViewHolder(binding)
    }

    override fun getItemCount() = regionList.size

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val item = regionList[position]
        holder.bind(item)
    }

    inner class RegionViewHolder(private val binding: ItemRegionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(region: FirestoreRegion) {
            binding.txtRegionName.text = region.name
            binding.root.setOnClickListener { onItemClick(region) }
        }
    }
}

