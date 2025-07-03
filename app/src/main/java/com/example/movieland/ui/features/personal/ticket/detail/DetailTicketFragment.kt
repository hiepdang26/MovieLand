package com.example.movieland.ui.features.personal.ticket.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.MainActivity
import com.example.movieland.R
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.data.firebase.model.ticket.SelectedCombo
import com.example.movieland.databinding.FragmentDetailTicketBinding
import com.example.movieland.ui.features.home.payment.detail.ComboBookedAdapter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DetailTicketFragment : BaseFragment<FragmentDetailTicketBinding>() {
    private var tickets: List<FirestoreTicket> = emptyList()
    private var bookingId: String? = null

    private val viewModel: DetailTicketViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailTicketBinding {
        return FragmentDetailTicketBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        arguments?.let {
            tickets = it.getParcelableArrayList<FirestoreTicket>("tickets") ?: emptyList()
            bookingId = it.getString("bookingId")

        }
        showTicketDetails()
    }
    private fun showTicketDetails() {
        val ticket = tickets.firstOrNull() ?: return
        val movieId = ticket.movieId ?: ""
        movieId?.let { viewModel.loadMovieDetail(it) }
        val mergedCombos = mergeCombos(tickets)
        val comboAdapter = ComboBookedAdapter(mergedCombos)
        binding.rcvCombo.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = comboAdapter
        }
        binding.txtNameMovie.text = ticket.movieName
        binding.txtGenre.text = "${ticket.screenType} • ${ticket.screenCategory}"
        binding.txtAddress.text = "${ticket.roomName} - ${ticket.districtName}"
        binding.txtSeat.text = tickets.joinToString(", ") { it.seatLabel }
        binding.txtDate.text = formatDate(ticket.showDate)
        binding.txtTime.text = formatTime(ticket.startTime)
        binding.txtTicketCode.text = ticket.ticketId ?: ""
        binding.txtPriceTicket.text = formatCurrency(tickets.sumOf { it.price })
    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
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
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
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