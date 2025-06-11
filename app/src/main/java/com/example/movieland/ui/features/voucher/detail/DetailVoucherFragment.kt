package com.example.movieland.ui.features.voucher.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.MainActivity
import com.example.movieland.R
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.example.movieland.databinding.FragmentDetailVoucherBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DetailVoucherFragment : BaseFragment<FragmentDetailVoucherBinding>() {

    private val viewModel: DetailVoucherViewModel by viewModels()
    private var voucherId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentDetailVoucherBinding {
       return  FragmentDetailVoucherBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
       _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun setupInitialData() {
        voucherId = arguments?.getString("voucherId") ?: return
        viewModel.loadVoucher(voucherId)
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.voucher.collectLatest { voucher ->
                voucher?.let { bindVoucherToUI(it) }
            }
        }
    }

    private fun bindVoucherToUI(voucher: FirestoreVoucher) {
        binding.apply {
            txtVoucherCode.text = "Mã: ${voucher.code}"
            txtDiscountInfo.text = when (voucher.discountType) {
                FirestoreVoucher.VoucherType.PERCENTAGE ->
                    "Giảm ${voucher.discountPercent?.toInt()}% - Tối đa ₫${voucher.maxDiscount?.toInt()}"
                FirestoreVoucher.VoucherType.FIXED ->
                    "Giảm ₫${voucher.discountAmount?.toInt()} - Tối đa ₫${voucher.maxDiscount?.toInt()}"
            }

            txtMinValue.text = "Đơn tối thiểu ₫${voucher.minTicketValue.toInt()}"
            txtExpiryDate.text = "Hiệu lực từ: ${voucher.startDate.formatDate()}"
            txtDiscountType.text = when (voucher.discountType) {
                FirestoreVoucher.VoucherType.PERCENTAGE -> "Phần trăm"
                FirestoreVoucher.VoucherType.FIXED -> "Giá cố định"
            }
            txtCreatedAt.text = voucher.createdAt.formatDate()
            txtValidityPeriod.text = "${voucher.startDate.formatFullDate()} - ${voucher.endDate.formatFullDate()}"
            txtDescription.text = voucher.description
        }
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()

    }
    fun Date.formatDate(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return sdf.format(this)
    }

    fun Date.formatFullDate(): String {
        val sdf = SimpleDateFormat("dd 'Th'MM yyyy HH:mm", Locale.getDefault())
        return sdf.format(this)
    }

}