package com.example.movieland.ui.features.home.combo


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieland.data.firebase.model.combo.FirestoreCombo
import com.example.movieland.databinding.ItemComboBinding

class ComboAdapter(
    private var combos: List<FirestoreCombo>,
    private val onIncrease: (FirestoreCombo) -> Unit,
    private val onDecrease: (FirestoreCombo) -> Unit
) : RecyclerView.Adapter<ComboAdapter.ComboViewHolder>() {

    private var counts: Map<String, Int> = emptyMap()

    fun submitList(newCombos: List<FirestoreCombo>) {
        combos = newCombos
        notifyDataSetChanged()
    }

    fun updateCounts(newCounts: Map<String, Int>) {
        counts = newCounts
        notifyDataSetChanged()
    }

    inner class ComboViewHolder(val binding: ItemComboBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemComboBinding.inflate(inflater, parent, false)
        return ComboViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComboViewHolder, position: Int) {
        val combo = combos[position]
        val count = counts[combo.id] ?: 0

        holder.binding.apply {
            txtName.text = combo.name
            txtPrice.text = "${combo.price} đ"
            txtQuantity.text = count.toString()

            Glide.with(imgProduct.context)
                .load(combo.imageUrl)
                .into(imgProduct)

            btnIncrease.setOnClickListener {
                onIncrease(combo)
            }

            btnDecrease.setOnClickListener {
                if (count > 0) {
                    onDecrease(combo)
                }
            }
        }
    }

    override fun getItemCount(): Int = combos.size
}
