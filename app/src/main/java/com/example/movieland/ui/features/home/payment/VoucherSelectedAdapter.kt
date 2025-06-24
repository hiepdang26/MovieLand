package com.example.movieland.ui.features.home.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.R
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.example.movieland.databinding.ItemVoucherSelectedBinding

class VoucherSelectedAdapter(
    private var vouchers: List<FirestoreVoucher>,
    private val onVoucherSelected: (FirestoreVoucher?) -> Unit
) : RecyclerView.Adapter<VoucherSelectedAdapter.VoucherViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class VoucherViewHolder(val binding: ItemVoucherSelectedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(voucher: FirestoreVoucher, isSelected: Boolean) {
            binding.txtCode.text = voucher.code
            binding.txtDiscount.text = when (voucher.discountType) {
                FirestoreVoucher.VoucherType.PERCENTAGE -> "Giảm ${voucher.discountPercent?.toInt()}%"
                FirestoreVoucher.VoucherType.FIXED -> "Giảm ${voucher.discountAmount?.toInt()}đ"
            }
            binding.root.isSelected = isSelected
            binding.root.background = if (isSelected) {
                binding.root.context.getDrawable(R.drawable.bg_voucher_selected)
            } else {
                null
            }

            binding.root.setOnClickListener {
                val oldSelected = selectedPosition
                if (adapterPosition == selectedPosition) {
                    selectedPosition = RecyclerView.NO_POSITION
                    notifyItemChanged(adapterPosition)
                    onVoucherSelected(null)
                } else {
                    selectedPosition = adapterPosition
                    notifyItemChanged(oldSelected)
                    notifyItemChanged(selectedPosition)
                    onVoucherSelected(voucher)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVoucherSelectedBinding.inflate(inflater, parent, false)
        return VoucherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        holder.bind(vouchers[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = vouchers.size

    fun submitList(newVouchers: List<FirestoreVoucher>) {
        vouchers = newVouchers
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}
