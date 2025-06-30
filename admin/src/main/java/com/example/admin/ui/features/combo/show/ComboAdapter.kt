package com.example.admin.ui.features.combo.show


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.data.firebase.model.combo.FirestoreCombo
import com.example.admin.databinding.ItemComboBinding
import java.text.NumberFormat
import java.util.Locale

class ComboAdapter(
    private val combos: List<FirestoreCombo>, private val onItemClick: (FirestoreCombo) -> Unit
) : RecyclerView.Adapter<ComboAdapter.ComboViewHolder>() {

    inner class ComboViewHolder(val binding: ItemComboBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemComboBinding.inflate(inflater, parent, false)
        return ComboViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComboViewHolder, position: Int) {
        val combo = combos[position]
        val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(combo.price)
        holder.binding.apply {
            txtName.text = combo.name
            txtPrice.text = "$formattedPrice đ"
            Glide.with(imgProduct.context).load(combo.imageUrl).into(imgProduct)
            txtStatus.text = when (combo.available) {
                true -> "Đang hoạt động"
                false -> "Ngưng hoạt động"
                null -> "Không xác định"

            }

            val color = when (combo.available) {
                true -> Color.GREEN
                false -> Color.RED
                else -> Color.GRAY
            }
            txtStatus.setTextColor(color)
            root.setOnClickListener {
                onItemClick(combo)
            }
        }
    }

    override fun getItemCount(): Int = combos.size
}
