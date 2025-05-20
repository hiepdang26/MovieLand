package com.example.admin.ui.features.district.show

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.firebase.model.district.FirestoreDistrict
import com.example.admin.databinding.ItemDistrictBinding

class DistrictAdapter : ListAdapter<FirestoreDistrict, DistrictAdapter.DistrictViewHolder>(DiffCallback()) {

    var onItemClick: ((FirestoreDistrict) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictViewHolder {
        val binding = ItemDistrictBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DistrictViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DistrictViewHolder, position: Int) {
        val district = getItem(position)
        holder.bind(district)
    }

    inner class DistrictViewHolder(private val binding: ItemDistrictBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(district: FirestoreDistrict) {
            binding.txtDistrictName.text = district.name
            binding.txtRegionName.text = district.regionName.toString()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FirestoreDistrict>() {
        override fun areItemsTheSame(oldItem: FirestoreDistrict, newItem: FirestoreDistrict) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FirestoreDistrict, newItem: FirestoreDistrict) = oldItem == newItem
    }
}
