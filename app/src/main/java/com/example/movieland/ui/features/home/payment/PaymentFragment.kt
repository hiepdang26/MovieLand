package com.example.movieland.ui.features.home.payment

import android.app.Activity
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.R
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.example.movieland.databinding.FragmentPaymentBinding
import com.example.movieland.ui.features.home.combo.ComboSelected
import com.example.movieland.ui.features.home.movie.show.ShowMovieFragment
import com.example.movieland.ui.features.home.payment.detail.DetailPaymentFragment
import com.example.movieland.ui.features.personal.ticket.detail.DetailTicketFragment
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import java.util.UUID

@AndroidEntryPoint
class PaymentFragment : BaseFragment<FragmentPaymentBinding>() {
    private var showtimeId: String = ""
    private var baseTotalPrice: Double = 0.0
    private var discountedPrice: Double = 0.0
    private var isPaymentCompleted = false
    private var selectedCombos: ArrayList<ComboSelected>? = null
    private var selectedTickets: ArrayList<FirestoreTicket>? = null
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 999

    private var selectedVoucher: FirestoreVoucher? = null
    private var selectedPaymentMethod: PaymentMethod? = null
    private lateinit var voucherAdapter: VoucherSelectedAdapter
    private val viewModel: PaymentViewModel by viewModels()
    private lateinit var paymentsClient: PaymentsClient

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
        discountedPrice = baseTotalPrice
        binding.txtPrice.text = formatPrice(discountedPrice)
        selectedTickets = arguments?.getParcelableArrayList("selectedTickets")
        binding.txtSeatName.text = selectedTickets?.joinToString(", ") { it.seatLabel }
        viewModel.loadShowtimes(showtimeId)
        viewModel.loadVouchers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        initGooglePayClient()
        lockSelectedTickets()
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
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = voucherAdapter
        }
    }

    private fun lockSelectedTickets() {
        selectedTickets?.forEach { ticket ->
            viewModel.updateTicketStatus(
                showtimeId = showtimeId,
                ticketId = ticket.ticketId,
                status = "locked",
                userId = currentUserId
            )
        }
    }

    private fun calculateDiscountedPrice() {
        discountedPrice = if (selectedVoucher == null) {
            baseTotalPrice
        } else {
            when (selectedVoucher!!.discountType) {
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
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
                    binding.txtShowTime.text =
                        "${formatTime(it.startTime)} - ${formatTime(it.endTime)}"
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
            handleBackPress()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            })
        binding.btnPayment.setOnClickListener {
            if (selectedPaymentMethod == null) {
                Toast.makeText(
                    requireContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.setTicketsBooking(
                showtimeId = showtimeId, tickets = selectedTickets.orEmpty(), userId = currentUserId
            ) { bookingSuccess, bookingError ->
                if (bookingSuccess) {
                    isPaymentCompleted = true
                    when (selectedPaymentMethod) {
                        PaymentMethod.GOOGLE_PAY -> startGooglePay()
                        else -> null
                    }
                } else {
                    showSeatErrorAndGoBack(
                        bookingError ?: "Ghế đã bị người khác đặt, vui lòng chọn lại."
                    )
                }
            }
        }
        binding.btnGooglePay.setOnClickListener {
            setSelectedPaymentMethod(PaymentMethod.GOOGLE_PAY)
        }

        binding.btnPayPal.setOnClickListener {
            setSelectedPaymentMethod(PaymentMethod.PAYPAL)
        }
    }

    private fun setSelectedPaymentMethod(method: PaymentMethod) {
        selectedPaymentMethod = method

        val defaultBackground = R.drawable.rounded_background_white
        val selectedBackground = R.drawable.rounded_border_selected

        binding.btnGooglePay.setBackgroundResource(
            if (method == PaymentMethod.GOOGLE_PAY) selectedBackground else defaultBackground
        )
        binding.btnPayPal.setBackgroundResource(
            if (method == PaymentMethod.PAYPAL) selectedBackground else defaultBackground
        )
    }

    private fun showSeatErrorAndGoBack(message: String) {
        AlertDialog.Builder(requireContext()).setTitle("Không thể thanh toán").setMessage(message)
            .setPositiveButton("Chọn lại ghế") { dialog, _ ->
                dialog.dismiss()
                parentFragmentManager.popBackStack("ChooseSeatFragment", 0)
            }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isPaymentCompleted) {
            selectedTickets?.forEach { ticket ->
                viewModel.resetTicketIfLockedByCurrentUser(
                    showtimeId = showtimeId, ticketId = ticket.ticketId, userId = currentUserId
                )
            }
        }
    }


    private fun handleBackPress() {
        parentFragmentManager.popBackStack()
    }

    private fun initGooglePayClient() {
        val walletOptions =
            Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
        paymentsClient = Wallet.getPaymentsClient(requireContext(), walletOptions)
    }

    private fun startGooglePay() {
        val paymentDataRequestJson =
            GooglePayJsonFactory.getPaymentDataRequest(discountedPrice.toString())
        if (paymentDataRequestJson == null) {
            Log.e("GooglePay", "paymentDataRequestJson is null")
            Toast.makeText(requireContext(), "Không thể tạo yêu cầu thanh toán", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        val task = paymentsClient.loadPaymentData(request)
        task.addOnCompleteListener { completedTask ->
            try {
                val paymentData = completedTask.getResult(ApiException::class.java)
                val paymentInfo = paymentData?.toJson()
                Log.d("GooglePay", "✅ Thanh toán thành công: $paymentInfo")
                Toast.makeText(requireContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    6 -> {
                        val resolvable = exception as ResolvableApiException
                        try {
                            startIntentSenderForResult(
                                resolvable.resolution.intentSender,
                                LOAD_PAYMENT_DATA_REQUEST_CODE,
                                null,
                                0,
                                0,
                                0,
                                null
                            )
                        } catch (sendEx: Exception) {
                            Log.e("GooglePay", "❌ Không thể mở Google Pay UI", sendEx)
                        }
                    }

                    else -> {
                        Log.e("GooglePay", "❌ Lỗi không xử lý được", exception)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val bookingId = UUID.randomUUID().toString()

                        viewModel.setTicketsBooked(
                            showtimeId = showtimeId,
                            bookingId = bookingId,
                            tickets = selectedTickets.orEmpty(),
                            userId = currentUserId,
                            price = discountedPrice
                        ) { success, errorMsg ->
                            if (success) {
                                Toast.makeText(
                                    requireContext(), "Thanh toán thành công", Toast.LENGTH_SHORT
                                ).show()
                                parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView, DetailPaymentFragment().apply {
                                        arguments = Bundle().apply {
                                            putString("bookingId", bookingId)
                                        }
                                    })
                                    .commit()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Lỗi cập nhật vé: $errorMsg",
                                    Toast.LENGTH_LONG
                                ).show()
                                parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView, ShowMovieFragment().apply {
                                        arguments = Bundle().apply {
                                        }
                                    })
                                    .commit()
                            }
                        }
                    }

                    Activity.RESULT_CANCELED, AutoResolveHelper.RESULT_ERROR -> {
                        Log.w("GooglePay", "Thanh toán bị hủy hoặc lỗi.")

                        viewModel.resetBookingToLocked(
                            showtimeId = showtimeId,
                            tickets = selectedTickets.orEmpty(),
                            userId = currentUserId
                        ) { success, errorMsg ->
                            if (success) {
                                Toast.makeText(
                                    requireContext(),
                                    "Đã huỷ thanh toán, ghế đã được giữ lại.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Lỗi khi reset vé: $errorMsg",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    else -> {
                        Log.e("GooglePay", "Unknown resultCode: $resultCode")
                    }
                }
            }
        }
    }
}
