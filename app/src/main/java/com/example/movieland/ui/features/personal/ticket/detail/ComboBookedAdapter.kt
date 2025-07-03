package com.example.movieland.ui.features.personal.ticket.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieland.R
import com.example.movieland.data.firebase.model.ticket.SelectedCombo
import com.example.movieland.databinding.ItemComboSelectedBinding
import com.example.movieland.ui.features.home.combo.ComboSelected
import java.text.NumberFormat
import java.util.Locale

class ComboBookedAdapter(private val combos: List<SelectedCombo>) : RecyclerView.Adapter<ComboBookedAdapter.ComboViewHolder>() {

    class ComboViewHolder(val binding: ItemComboSelectedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboViewHolder {
        val binding = ItemComboSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComboViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComboViewHolder, position: Int) {
        val item = combos[position]
        holder.binding.apply {
            txtName.text = item.name
            txtQuantity.text = "x${item.quantity}"
            txtPrice.text = formatCurrencyVND(item.price * item.quantity)

            Glide.with(imgProduct.context)
                .load(R.drawable.img_popcorn)
                .into(imgProduct)
        }
    }


    fun formatCurrencyVND(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        val str = formatter.format(amount)
        return str.replace("₫", "đ")
    }

    override fun getItemCount(): Int = combos.size
}

