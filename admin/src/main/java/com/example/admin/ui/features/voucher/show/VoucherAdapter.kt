package com.example.admin.ui.features.voucher.show


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.firebase.model.voucher.FirestoreVoucher
import com.example.admin.databinding.ItemVoucherBinding

class VoucherAdapter(
    private val vouchers: List<FirestoreVoucher>,
    private val onItemClick: (FirestoreVoucher) -> Unit
) : RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    inner class VoucherViewHolder(val binding: ItemVoucherBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVoucherBinding.inflate(inflater, parent, false)
        return VoucherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = vouchers[position]
        holder.binding.apply {
            txtCode.text = voucher.code
            txtDescription.text = voucher.description
            txtDiscount.text = when (voucher.discountType) {
                FirestoreVoucher.VoucherType.PERCENTAGE -> "Giảm ${voucher.discountPercent?.toInt()}%"
                FirestoreVoucher.VoucherType.FIXED -> "Giảm ${voucher.discountAmount?.toInt()}đ"
            }
            txtValidTime.text = "Từ ${voucher.startDate} đến ${voucher.endDate}"
            txtStatus.text = if (voucher.isActive) "Đang hoạt động" else "Ngưng hoạt động"

            root.setOnClickListener {
                onItemClick(voucher)
            }
        }
    }

    override fun getItemCount(): Int = vouchers.size
}

