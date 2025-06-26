package com.example.movieland.ui.features.home.regionanddistrict

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.R
import com.example.movieland.data.firebase.model.district.FirestoreDistrict
import com.example.movieland.databinding.ItemDistrictBinding

class DistrictAdapter(
    private val onItemClick: (FirestoreDistrict) -> Unit,
) : RecyclerView.Adapter<DistrictAdapter.DistrictViewHolder>() {

    private var districtList = mutableListOf<FirestoreDistrict>()
    private var selectedPosition = RecyclerView.NO_POSITION

    fun submitList(data: List<FirestoreDistrict>) {
    Log.d("DistrictAdapter", "submitList size=${data.size} -- data=${data.joinToString { it.name }}")

        districtList.clear()
        districtList.addAll(data)
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictViewHolder {
        val binding =
            ItemDistrictBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DistrictViewHolder(binding)
    }

    override fun getItemCount() = districtList.size

    override fun onBindViewHolder(holder: DistrictViewHolder, position: Int) {
        val item = districtList[position]
        holder.bind(item, position == selectedPosition)
    }

    inner class DistrictViewHolder(private val binding: ItemDistrictBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(district: FirestoreDistrict, isSelected: Boolean) {
            binding.txtDistrictName.text = district.name
            if (isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_item_selected)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_item_default)
            }

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemClick(district)
            }
        }
    }
}

