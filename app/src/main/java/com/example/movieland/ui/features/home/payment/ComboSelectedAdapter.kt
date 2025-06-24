package com.example.movieland.ui.features.home.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieland.data.firebase.model.combo.FirestoreCombo
import com.example.movieland.databinding.ItemComboSelectedBinding
import com.example.movieland.ui.features.home.combo.ComboSelected

class ComboSelectedAdapter(
    private var combos: List<SelectedComboItem>
) : RecyclerView.Adapter<ComboSelectedAdapter.ComboSelectedViewHolder>() {

    data class SelectedComboItem(
        val combo: ComboSelected,
        val count: Int
    )

    inner class ComboSelectedViewHolder(val binding: ItemComboSelectedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboSelectedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemComboSelectedBinding.inflate(inflater, parent, false)
        return ComboSelectedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComboSelectedViewHolder, position: Int) {
        val item = combos[position]
        holder.binding.apply {
            txtName.text = item.combo.comboName
            txtQuantity.text = "x${item.count}"
            txtPrice.text = "${item.combo.comboPrice * item.count} đ"

            Glide.with(imgProduct.context)
                .load(item.combo.comboImageUrl)
                .into(imgProduct)
        }
    }

    override fun getItemCount(): Int = combos.size

    fun submitList(newCombos: List<SelectedComboItem>) {
        combos = newCombos
        notifyDataSetChanged()
    }
}
