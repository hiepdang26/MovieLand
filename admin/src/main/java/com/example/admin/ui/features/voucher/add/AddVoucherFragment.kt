package com.example.admin.ui.features.voucher.add

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.data.firebase.model.voucher.FirestoreVoucher
import com.example.admin.databinding.FragmentAddVoucherBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Date

@AndroidEntryPoint
class AddVoucherFragment : BaseFragment<FragmentAddVoucherBinding>() {

    private val viewModel: AddVoucherViewModel by viewModels()
    private var selectedEndDate: Date? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddVoucherBinding {
        return FragmentAddVoucherBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupClickView()
        setupObserver()
    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
    }
    override fun setupClickView() {
        binding.edtEndDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val datePicker = android.app.DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth, 0, 0, 0)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    selectedEndDate = calendar.time
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("vi", "VN"))
                    binding.edtEndDate.setText(sdf.format(selectedEndDate!!))
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnSaveVoucher.setOnClickListener {
            val code = binding.edtCode.text.toString().trim()
            val desc = binding.edtDescription.text.toString().trim()
            val typeIndex = binding.spinnerDiscountType.selectedItemPosition
            val type = if (typeIndex == 0) FirestoreVoucher.VoucherType.PERCENTAGE else FirestoreVoucher.VoucherType.FIXED

            val percent = binding.edtDiscountPercent.text.toString().toDoubleOrNull()
            val amount = binding.edtDiscountAmount.text.toString().toDoubleOrNull()
            val max = binding.edtMaxDiscount.text.toString().toDoubleOrNull()
            val minValue = binding.edtMinTicketValue.text.toString().toDoubleOrNull() ?: 0.0
            val usageLimit = binding.edtUsageLimit.text.toString().toIntOrNull() ?: 1
            val status = binding.chkActive.isChecked

            if (code.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập mã voucher", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedEndDate == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn ngày hết hạn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val now = Date()
            val endDate = selectedEndDate ?: now

            val voucher = FirestoreVoucher(
                id = "voucher_${System.currentTimeMillis()}",
                code = code,
                description = desc,
                discountType = type,
                discountPercent = if (type == FirestoreVoucher.VoucherType.PERCENTAGE) percent else null,
                discountAmount = if (type == FirestoreVoucher.VoucherType.FIXED) amount else null,
                maxDiscount = max,
                minTicketValue = minValue,
                usageLimit = usageLimit,
                usedCount = 0,
                isActive = status,
                startDate = now,
                endDate = endDate,
                createdAt = now,
                updatedAt = now
            )

            if (type == FirestoreVoucher.VoucherType.PERCENTAGE && percent == null) {
                Toast.makeText(requireContext(), "Vui lòng nhập phần trăm giảm", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (type == FirestoreVoucher.VoucherType.FIXED && amount == null) {
                Toast.makeText(requireContext(), "Vui lòng nhập số tiền giảm", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addVoucher(voucher)
        }


    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.success.collectLatest {
                if (it) {
                    Toast.makeText(requireContext(), "Thêm voucher thành công", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                it?.let {
                    Toast.makeText(requireContext(), "Lỗi: $it", Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
    }

    override fun setupInitialData() {
        val discountTypes = listOf("Phần trăm", "Cố định")
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_white,
            discountTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDiscountType.adapter = adapter

        binding.spinnerDiscountType.adapter = adapter
        binding.spinnerDiscountType.setSelection(0)

        binding.spinnerDiscountType.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    binding.layoutDiscountPercent.visibility = View.VISIBLE
                    binding.layoutDiscountAmount.visibility = View.GONE
                } else {
                    binding.layoutDiscountPercent.visibility = View.GONE
                    binding.layoutDiscountAmount.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

}
