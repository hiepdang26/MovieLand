package com.example.movieland.ui.features.home.regionanddistrict

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.data.firebase.model.district.FirestoreDistrict
import com.example.movieland.databinding.ItemDistrictBinding

class DistrictAdapter(
    private val onItemClick: (FirestoreDistrict) -> Unit,
) : RecyclerView.Adapter<DistrictAdapter.DistrictViewHolder>() {

    private var districtList = listOf<FirestoreDistrict>()

    fun submitList(data: List<FirestoreDistrict>) {
        districtList = data
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
        holder.bind(item)
    }

    inner class DistrictViewHolder(private val binding: ItemDistrictBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(district: FirestoreDistrict) {
            binding.txtDistrictName.text = district.name
            binding.root.setOnClickListener {
                onItemClick(district)
            }
        }
    }
}

