package com.example.admin.ui.features.voucher.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.admin.data.firebase.model.voucher.FirestoreVoucher
import com.example.admin.databinding.FragmentEditVoucherBinding
import com.example.admin.ui.bases.BaseFragment
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditVoucherFragment : BaseFragment<FragmentEditVoucherBinding>() {

    private val viewModel: EditVoucherViewModel by viewModels()
    private var voucherId = ""


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditVoucherBinding {
        return FragmentEditVoucherBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        voucherId = arguments?.getString("voucherId") ?: return
        setupSpinners()
        viewModel.loadVoucher(voucherId)
        setupObserver()
        setupClickView()
    }

    private fun setupSpinners() {
        val discountTypes = listOf("Phần trăm", "Cố định")
        val discountTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            discountTypes
        )
        discountTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDiscountType.adapter = discountTypeAdapter


    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.voucher.collectLatest { voucher ->
                voucher?.let { bindVoucherToUI(it) }
            }
        }

        lifecycleScope.launch {
            viewModel.updateSuccess.collectLatest {
                Toast.makeText(requireContext(), "✔️ Cập nhật thành công", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        lifecycleScope.launch {
            viewModel.deleteSuccess.collectLatest {
                Toast.makeText(requireContext(), "🗑️ Đã xoá voucher", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindVoucherToUI(voucher: FirestoreVoucher) {
        binding.apply {
            edtCode.setText(voucher.code)
            edtDescription.setText(voucher.description)
            edtMinTicketValue.setText(voucher.minTicketValue.toString())
            edtDiscountPercent.setText(voucher.discountPercent?.toString() ?: "")
            edtDiscountAmount.setText(voucher.discountAmount?.toString() ?: "")
            edtMaxDiscount.setText(voucher.maxDiscount?.toString() ?: "")
            edtUsageLimit.setText(voucher.usageLimit.toString())
            chkActive.isChecked = voucher.isActive

            spinnerDiscountType.setSelection(
                when (voucher.discountType) {
                    FirestoreVoucher.VoucherType.PERCENTAGE -> 0
                    FirestoreVoucher.VoucherType.FIXED -> 1
                }
            )

            chkActive.isChecked = voucher.isActive
        }
    }

    override fun setupClickView() {
        binding.btnSaveVoucher.setOnClickListener {
            val code = binding.edtCode.text.toString().trim()
            val description = binding.edtDescription.text.toString().trim()
            val minValue = binding.edtMinTicketValue.text.toString().toDoubleOrNull() ?: 0.0
            val percent = binding.edtDiscountPercent.text.toString().toDoubleOrNull()
            val amount = binding.edtDiscountAmount.text.toString().toDoubleOrNull()
            val maxDiscount = binding.edtMaxDiscount.text.toString().toDoubleOrNull()
            val usageLimit = binding.edtUsageLimit.text.toString().toIntOrNull() ?: 1
            val isActive = binding.chkActive.isChecked

            val typeIndex = binding.spinnerDiscountType.selectedItemPosition
            val type = if (typeIndex == 0) FirestoreVoucher.VoucherType.PERCENTAGE else FirestoreVoucher.VoucherType.FIXED

            val updateMap = mutableMapOf<String, Any?>(
                "code" to code,
                "description" to description,
                "minTicketValue" to minValue,
                "discountPercent" to percent,
                "discountAmount" to amount,
                "maxDiscount" to maxDiscount,
                "usageLimit" to usageLimit,
                "isActive" to isActive,
                "discountType" to type.name,
                "updatedAt" to Timestamp.now()
            )

            viewModel.updateVoucher(voucherId, updateMap)
        }

        binding.btnDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá voucher này?")
                .setPositiveButton("Xoá") { _, _ ->
                    viewModel.deleteVoucher(voucherId)
                }
                .setNegativeButton("Huỷ", null)
                .show()
        }


        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun setupInitialData() {}
}
