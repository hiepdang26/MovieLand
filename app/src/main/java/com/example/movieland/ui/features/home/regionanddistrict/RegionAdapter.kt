package com.example.movieland.ui.features.home.regionanddistrict

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.R
import com.example.movieland.data.firebase.model.region.FirestoreRegion
import com.example.movieland.databinding.ItemRegionBinding

class RegionAdapter(
    private val onItemClick: (FirestoreRegion) -> Unit
) : RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {

    private var regionList = listOf<FirestoreRegion>()
    private var selectedPosition = RecyclerView.NO_POSITION  // vị trí được chọn

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
        holder.bind(item, position == selectedPosition)
    }

    inner class RegionViewHolder(private val binding: ItemRegionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(region: FirestoreRegion, isSelected: Boolean) {
            binding.txtRegionName.text = region.name

            // Cập nhật UI khi được chọn hoặc không
            if (isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_item_selected) // drawable viền và màu nền
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_item_default) // background mặc định
            }

            binding.root.setOnClickListener {
                // Cập nhật vị trí chọn mới
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                Log.d("RegionAdapter", "Bạn vừa chọn region: id=${region.id}, name=${region.name}")

                onItemClick(region)
            }
        }
    }
}

