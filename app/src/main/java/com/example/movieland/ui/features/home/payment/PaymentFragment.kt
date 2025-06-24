package com.example.movieland.ui.features.home.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.example.movieland.databinding.FragmentPaymentBinding
import com.example.movieland.ui.features.home.combo.ComboSelected
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
@AndroidEntryPoint
class PaymentFragment : BaseFragment<FragmentPaymentBinding>() {

    private var selectedCombos: ArrayList<ComboSelected>? = null
    private var showtimeId: String = ""
    private var baseTotalPrice: Double = 0.0
    private var discountedPrice: Double = 0.0
    private var selectedVoucher: FirestoreVoucher? = null

    private lateinit var voucherAdapter: VoucherSelectedAdapter

    private val viewModel: PaymentViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPaymentBinding = FragmentPaymentBinding.inflate(inflater, container, false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun setupInitialData() {
        selectedCombos = arguments?.getParcelableArrayList("selectedCombos")
        showtimeId = arguments?.getString("showtimeId") ?: ""
        baseTotalPrice = arguments?.getDouble("totalPrice") ?: 0.0
        Log.d("PaymentFragment", "showtimeId = $showtimeId")


        selectedCombos?.forEach {
            Log.d("PaymentFragment", "comboId: ${it.comboId} ${it.quantity}  ")
        }

        binding.txtPrice.text = baseTotalPrice.toString()
        viewModel.loadShowtimes(showtimeId)
        viewModel.loadVouchers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupComboRecyclerView()
        setupVoucherRecyclerView()
        setupClickView()
        setupObserver()
    }
    private fun setupVoucherRecyclerView() {
        voucherAdapter = VoucherSelectedAdapter(emptyList()) { voucher ->
            selectedVoucher = voucher
            calculateDiscountedPrice()
        }
        binding.rcvVoucher.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = voucherAdapter
        }
    }

    private fun calculateDiscountedPrice() {
        if (selectedVoucher == null) {
            discountedPrice = baseTotalPrice
        } else {
            discountedPrice = when(selectedVoucher!!.discountType) {
                FirestoreVoucher.VoucherType.PERCENTAGE -> {
                    val percent = selectedVoucher!!.discountPercent ?: 0.0
                    val maxDiscount = selectedVoucher!!.maxDiscount ?: Double.MAX_VALUE
                    val discount = (baseTotalPrice * percent / 100).coerceAtMost(maxDiscount)
                    baseTotalPrice - discount
                }
                FirestoreVoucher.VoucherType.FIXED -> {
                    val fixedAmount = selectedVoucher!!.discountAmount ?: 0.0
                    (baseTotalPrice - fixedAmount).coerceAtLeast(0.0)
                }
            }
        }
        binding.txtPrice.text = formatPrice(discountedPrice)
    }

    private fun setupComboRecyclerView() {
        val selectedComboItems = selectedCombos?.map { combo ->
            ComboSelectedAdapter.SelectedComboItem(combo, combo.quantity)
        } ?: emptyList()

        binding.rcvCombo.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ComboSelectedAdapter(selectedComboItems)
        }
    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.showtimes.collectLatest { showtime ->
                showtime?.let {
                    Log.d("PaymentFragment", " = $showtime")
                    binding.txtNameMovie.text = it.movieName
                    binding.txtGenre.text = "${it.screenType} | ${it.screenCategory}"
                    binding.txtNamePlace.text = it.districtName
                    binding.txtShowDate.text = formatDate(it.date)
                    binding.txtShowTime.text = "${formatTime(it.startTime)} - ${formatTime(it.endTime)}"
                    binding.txtRoomName.text = it.roomName
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.vouchers.collectLatest { vouchers ->
                vouchers?.let {
                    voucherAdapter.submitList(it)
                }
            }
        }

    }
    private fun formatPrice(price: Double): String {
        return String.format("%,.0f đ", price)
    }

    private fun formatDate(date: Date?): String {
        return date?.let {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            sdf.format(it)
        } ?: ""
    }

    private fun formatTime(date: Date?): String {
        return date?.let {
            SimpleDateFormat("HH:mm").format(it)
        } ?: ""
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


}
