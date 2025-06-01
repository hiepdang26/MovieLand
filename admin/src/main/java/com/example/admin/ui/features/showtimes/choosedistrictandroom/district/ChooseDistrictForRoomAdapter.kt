package com.example.admin.ui.features.showtimes.choosedistrictandroom.district

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemDistrictBinding
import com.example.admin.data.firebase.model.district.FirestoreDistrict

class ChooseDistrictForRoomAdapter(
private var districts: MutableList<FirestoreDistrict>,
private val onDistrictClick: (FirestoreDistrict) -> Unit
) : RecyclerView.Adapter<ChooseDistrictForRoomAdapter.DistrictViewHolder>() {

    inner class DistrictViewHolder(private val binding: ItemDistrictBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(district: FirestoreDistrict) {
            binding.txtDistrictName.text = district.name
            binding.root.setOnClickListener {
                onDistrictClick(district)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictViewHolder {
        val binding = ItemDistrictBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DistrictViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DistrictViewHolder, position: Int) {
        holder.bind(districts[position])
    }

    override fun getItemCount(): Int = districts.size

    fun updateData(newDistricts: List<FirestoreDistrict>) {
        districts.clear()
        districts.addAll(newDistricts)
        notifyDataSetChanged()
    }
}

