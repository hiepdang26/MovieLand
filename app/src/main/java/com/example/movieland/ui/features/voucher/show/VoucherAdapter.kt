package com.example.movieland.ui.features.voucher.show


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.example.movieland.databinding.ItemVoucherBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

        val startDateStr = voucher.startDate?.let { dateFormat.format(it) } ?: ""
        val endDateStr = voucher.endDate?.let { dateFormat.format(it) } ?: ""

        holder.binding.apply {
            txtCode.text = voucher.code
            txtDiscount.text = when (voucher.discountType) {
                FirestoreVoucher.VoucherType.PERCENTAGE -> "Giảm ${voucher.discountPercent?.toInt()}%"
                FirestoreVoucher.VoucherType.FIXED -> "Giảm ${voucher.discountAmount?.toInt()}đ"
            }
            txtValidTime.text = "Từ $startDateStr đến $endDateStr"

            root.setOnClickListener {
                onItemClick(voucher)
            }
        }
    }

    override fun getItemCount(): Int = vouchers.size
}

