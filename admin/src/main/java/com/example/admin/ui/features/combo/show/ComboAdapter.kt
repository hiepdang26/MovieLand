package com.example.admin.ui.features.combo.show


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.data.firebase.model.combo.FirestoreCombo
import com.example.admin.databinding.ItemComboBinding

class ComboAdapter(
    private val combos: List<FirestoreCombo>,
    private val onItemClick: (FirestoreCombo) -> Unit
) : RecyclerView.Adapter<ComboAdapter.ComboViewHolder>() {

    inner class ComboViewHolder(val binding: ItemComboBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemComboBinding.inflate(inflater, parent, false)
        return ComboViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComboViewHolder, position: Int) {
        val combo = combos[position]
        holder.binding.apply {
            txtName.text = combo.name
            txtPrice.text = "${combo.price} đ"
            Glide.with(imgProduct.context)
                .load(combo.imageUrl)
                .into(imgProduct)

            root.setOnClickListener {
                onItemClick(combo)
            }
        }
    }

    override fun getItemCount(): Int = combos.size
}
