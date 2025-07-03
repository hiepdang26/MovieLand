package com.example.movieland.ui.features.home.payment.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.R
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.data.firebase.model.ticket.SelectedCombo
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
        setupObserver()
    }
    override fun setupInitialData() {
        val bookingId = arguments?.getString("bookingId") ?: return
        viewModel.fetchTicketsByBookingId(bookingId) { tickets ->
            if (tickets.isNotEmpty()) {
                val ticket = tickets.first()
                val movieId = ticket.movieId ?: ""
                val mergedCombos = mergeCombos(tickets)
                val comboAdapter = ComboBookedAdapter(mergedCombos)
                binding.rcvCombo.apply {
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = comboAdapter
                }
                movieId?.let { viewModel.loadMovieDetail(it) }
                binding.txtNameMovie.text = ticket.movieName
                binding.txtGenre.text = "${ticket.screenType} • ${ticket.screenCategory}"
                binding.txtAddress.text = "${ticket.roomName} - ${ticket.districtName}"
                binding.txtSeat.text = tickets.joinToString(", ") { it.seatLabel }
                binding.txtDate.text = formatDate(ticket.showDate)
                binding.txtTime.text = formatTime(ticket.startTime)
                binding.txtTicketCode.text = ticket.ticketId
                binding.txtPriceTicket.text = formatCurrency(tickets.sumOf { it.price })
            }
        }
    }

    override fun setupObserver() {
        viewModel.movieDetail.observe(viewLifecycleOwner) { detail ->
            detail?.let {
                Glide.with(requireContext())
                    .load(it.posterPath)
                    .into(binding.imgMovie)
            }
        }
    }

    override fun setupClickView() {
        binding.btnHome.setOnClickListener {
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ShowMovieFragment())
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
    private fun mergeCombos(tickets: List<FirestoreTicket>): List<SelectedCombo> {
        val comboMap = mutableMapOf<String, SelectedCombo>()

        for (ticket in tickets) {
            for (combo in ticket.combos) {
                val exist = comboMap[combo.comboId]
                if (exist == null) {
                    comboMap[combo.comboId] = combo.copy()
                } else {
                    comboMap[combo.comboId] = exist.copy(
                        quantity = combo.quantity
                    )
                }
            }
        }
        return comboMap.values.toList()
    }

}