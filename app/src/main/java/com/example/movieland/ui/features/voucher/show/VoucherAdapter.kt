package com.example.movieland.ui.features.voucher.show


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.example.movieland.databinding.ItemVoucherBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class VoucherAdapter(
    private var vouchers: List<FirestoreVoucher>,
    private val onItemClick: (FirestoreVoucher) -> Unit
) : RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    inner class VoucherViewHolder(val binding: ItemVoucherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(voucher: FirestoreVoucher) {
            fun formatCurrency(amount: Int): String {
                val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("vi", "VN"))
                return formatter.format(amount) + " đ"
            }

            binding.txtCode.text = voucher.code

            binding.txtDiscount.text = when (voucher.discountType) {
                FirestoreVoucher.VoucherType.PERCENTAGE -> {
                    val percent = voucher.discountPercent?.toInt() ?: 0
                    val maxDiscount = voucher.maxDiscount?.toInt() ?: 0
                    val maxText = if (maxDiscount > 0) " (giảm tối đa ${formatCurrency(maxDiscount)})" else ""
                    "Giảm $percent%$maxText"
                }
                FirestoreVoucher.VoucherType.FIXED -> {
                    val amount = formatCurrency(voucher.discountAmount?.toInt() ?: 0)
                    val maxDiscount = voucher.maxDiscount?.toInt() ?: 0
                    val maxText = if (maxDiscount > 0) " (giảm tối đa ${formatCurrency(maxDiscount)})" else ""
                    "Giảm $amount$maxText"
                }
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

            val startDateStr = voucher.startDate?.let { dateFormat.format(it) } ?: ""
            val endDateStr = voucher.endDate?.let { dateFormat.format(it) } ?: ""

            val now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
            val endCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")).apply {
                time = voucher.endDate
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val isExpired = now.after(endCal)
            val usageLeft = voucher.usageLimit ?: 0

            if (usageLeft < 1) {
                binding.txtValidTime.text = "Hết lượt sử dụng"
                binding.root.isEnabled = false
                binding.root.alpha = 0.5f
            } else if (isExpired) {
                binding.txtValidTime.text = "Đã hết hạn"
                binding.root.isEnabled = false
                binding.root.alpha = 0.5f
            } else {
                binding.txtValidTime.text = "HSD: $endDateStr"
                binding.root.isEnabled = true
                binding.root.alpha = 1f
            }

            binding.root.setOnClickListener {
                if (usageLeft < 1 || isExpired) return@setOnClickListener
                onItemClick(voucher)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVoucherBinding.inflate(inflater, parent, false)
        return VoucherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        holder.bind(vouchers[position])
    }

    override fun getItemCount(): Int = vouchers.size

    fun submitList(newVouchers: List<FirestoreVoucher>) {
        vouchers = newVouchers
        notifyDataSetChanged()
    }
}


