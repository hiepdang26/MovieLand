package com.example.movieland.ui.features.home.payment.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.R
import com.example.movieland.databinding.FragmentDetailPaymentBinding
import com.example.movieland.ui.features.home.movie.show.ShowMovieFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DetailPaymentFragment : BaseFragment<FragmentDetailPaymentBinding>() {

    private val viewModel: DetailPaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailPaymentBinding {
       return FragmentDetailPaymentBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       setupInitialData()
        setupClickView()
    }
    override fun setupInitialData() {
        val bookingId = arguments?.getString("bookingId") ?: return
        viewModel.fetchTicketsByBookingId(bookingId) { tickets ->
            if (tickets.isNotEmpty()) {
                val ticket = tickets.first()
                binding.txtNameMovie.text = ticket.movieName
                binding.txtGenre.text = "${ticket.screenType} • ${ticket.screenCategory}"
                binding.txtAddress.text = "${ticket.roomName} | ${ticket.regionName} - ${ticket.districtName}"
                binding.txtSeat.text = tickets.joinToString(", ") { it.seatLabel }
                binding.txtDate.text = formatDate(ticket.showDate)
                binding.txtTime.text = formatTime(ticket.startTime)
                binding.txtTicketCode.text = ticket.ticketId
                binding.txtPriceTicket.text = formatCurrency(tickets.sumOf { it.price })
            }
        }
    }

    override fun setupObserver() {

    }

    override fun setupClickView() {
        binding.btnHome.setOnClickListener {
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ShowMovieFragment()) // thay bằng fragment home của bạn
                .commit()
        }
    }
    private fun formatDate(date: Date?): String {
        if (date == null) return ""
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        return sdf.format(date)
    }

    private fun formatTime(date: Date?): String {
        if (date == null) return ""
        val sdf = SimpleDateFormat("HH:mm", Locale("vi", "VN"))
        return sdf.format(date)
    }

    private fun formatCurrency(amount: Double): String {
        val formatter = java.text.NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        val str = formatter.format(amount)
        return str.replace("₫", "đ")
    }
}