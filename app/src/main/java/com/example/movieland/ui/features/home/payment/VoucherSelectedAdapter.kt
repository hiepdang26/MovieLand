package com.example.movieland.ui.features.home.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.R
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.example.movieland.databinding.ItemVoucherSelectedBinding
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class VoucherSelectedAdapter(
    private var vouchers: List<FirestoreVoucher>,
    private val onVoucherSelected: (FirestoreVoucher?) -> Unit
) : RecyclerView.Adapter<VoucherSelectedAdapter.VoucherViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class VoucherViewHolder(val binding: ItemVoucherSelectedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(voucher: FirestoreVoucher, isSelected: Boolean) {
            // Hiện txtEndDate chắc chắn
            binding.txtEndDate.visibility = View.VISIBLE

            // Format số tiền kiểu VN
            fun formatCurrency(amount: Int): String {
                val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
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

            // Tính hết hạn: Luôn cho phép dùng đến 23:59:59 ngày endDate (theo Asia/Ho_Chi_Minh)
            val now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
            val endCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")).apply {
                time = voucher.endDate
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val isExpired = now.after(endCal)

            // Set trạng thái hết hạn hoặc ngày hết hạn
            if (isExpired) {
                binding.txtEndDate.text = "Đã hết hạn"
                binding.root.isEnabled = false
                binding.root.alpha = 0.5f
            } else {
                val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
                }
                binding.txtEndDate.text = "HSD: ${sdf.format(voucher.endDate)}"
                binding.root.isEnabled = true
                binding.root.alpha = 1f
            }

            // Trạng thái được chọn
            binding.root.isSelected = isSelected
            binding.root.background = if (isSelected) {
                binding.root.context.getDrawable(R.drawable.bg_voucher_selected)
            } else {
                null
            }

            // Xử lý click
            binding.root.setOnClickListener {
                if (isExpired) return@setOnClickListener
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
