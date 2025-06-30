package com.example.movieland.ui.features.home.roomandseat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.MainActivity
import com.example.movieland.R
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.databinding.BottomSheetSelectedSeatsBinding
import com.example.movieland.databinding.FragmentChooseSeatBinding
import com.example.movieland.ui.features.home.combo.ChooseComboFragment
import com.example.movieland.ui.features.home.roomandseat.model.Seat
import com.example.movieland.ui.features.home.roomandseat.model.SeatLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ChooseSeatFragment : BaseFragment<FragmentChooseSeatBinding>() {
    private var roomId: String = ""
    private var showtimeId: String = ""
    private var movieName: String = ""
    private var districtId: String = ""
    private var showtimePrice: Double = 0.0

    private lateinit var seatLayout: SeatLayout
    private lateinit var seatAdapter: SeatAdapter
    private val viewModel: ChooseSeatViewModel by viewModels()
    private val selectedSeats = mutableListOf<Seat>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentChooseSeatBinding {
        return FragmentChooseSeatBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.zoomLayout.setHasClickableChildren(true)

        seatAdapter = SeatAdapter(emptyList()) { seat, position ->
            if (!seat.isDummy && seat.ticket?.status != "booked") {
                if (!seat.isSelected && selectedSeats.size >= 8) {
                    Toast.makeText(requireContext(), "Bạn chỉ được chọn tối đa 8 ghế", Toast.LENGTH_SHORT).show()
                    return@SeatAdapter
                }

                seat.isSelected = !seat.isSelected
                if (seat.isSelected) {
                    selectedSeats.add(seat)
                } else {
                    selectedSeats.remove(seat)
                }
                seatAdapter.notifyItemChanged(position)
            }
        }

        binding.recyclerViewSeats.apply {
            layoutManager = GridLayoutManager(requireContext(), 8)
            adapter = seatAdapter
            setHasFixedSize(true)
        }

        setupInitialData()
        setupObserver()
        setupClickView()
    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()

    }
    override fun setupInitialData() {
        roomId = arguments?.getString("roomId") ?: return
        showtimeId = arguments?.getString("showtimeId") ?: return
        movieName = arguments?.getString("movieName") ?: return
        districtId = arguments?.getString("districtId") ?: return
        showtimePrice = arguments?.getString("showtimePrice")?.toDoubleOrNull() ?: 0.0
        binding.txtNameMovie.text = movieName
        viewModel.loadRoomDetail(roomId)
        viewModel.listenTicketsRealtime(showtimeId)
    }


    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.room.collectLatest { room ->
                room?.let {
                    parseLayoutJson(it.layoutJson)
                    viewModel.loadTickets(showtimeId) { tickets ->
                        updateSeatsWithTickets(tickets)
                    }
                    viewModel.listenTicketsRealtime(showtimeId)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.tickets.collectLatest { tickets ->
                val now = System.currentTimeMillis()
                val MAX_HOLD_TIME = 1 * 20 * 1000L
                viewModel.autoReleaseExpiredTickets(tickets, MAX_HOLD_TIME)

                if (!::seatLayout.isInitialized || seatLayout.seats.isEmpty()) return@collectLatest

                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                val updatedSeats = seatLayout.seats.map { seat ->
                    val matchedTicket = tickets.find { it.seatLabel == seat.label }
                    seat.ticket = matchedTicket
                    val isLockedByUser = matchedTicket?.status == "locked" && matchedTicket.userId == currentUserId
                    seat.isSelected = isLockedByUser
                    if (isLockedByUser && seat !in selectedSeats) {
                        selectedSeats.add(seat)
                    } else if (!isLockedByUser && seat in selectedSeats) {
                        selectedSeats.remove(seat)
                    }
                    seat
                }

                seatAdapter.updateSeats(updatedSeats)
            }
        }
    }
    private fun updateSeatsWithTickets(tickets: List<FirestoreTicket>) {
        if (!::seatLayout.isInitialized || seatLayout.seats.isEmpty()) return

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val selectedSeatLabels = selectedSeats.map { it.label }.toSet()

        seatLayout.seats.forEach { seat ->
            val matchedTicket = tickets.find { it.seatLabel == seat.label }
            seat.ticket = matchedTicket

            val isLockedByUser = matchedTicket?.status == "locked" && matchedTicket.userId == currentUserId
            val isLockedByOthers = matchedTicket?.status == "locked" && matchedTicket.userId != currentUserId

            seat.isSelected = isLockedByUser || selectedSeatLabels.contains(seat.label)
        }

        selectedSeats.clear()
        selectedSeats.addAll(seatLayout.seats.filter { it.isSelected })

        seatAdapter.updateSeats(seatLayout.seats)
    }


    override fun setupClickView() {
        binding.btnContinue.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng chọn ghế trước khi tiếp tục",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (hasInvalidGap(selectedSeats, seatLayout.seats)) {
            } else {
                showSelectedSeatsDialog()
            }
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun parseLayoutJson(layoutJson: String) {
        seatLayout = Gson().fromJson(layoutJson, SeatLayout::class.java)
        seatLayout.seats.forEach { seat ->
            seat.position = seat.column - 1
        }
        (binding.recyclerViewSeats.layoutManager as? GridLayoutManager)?.spanCount = seatLayout.columns
    }



    private fun showSelectedSeatsDialog() {
        val dialogBinding = BottomSheetSelectedSeatsBinding.inflate(layoutInflater)
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val seatLabels = selectedSeats.joinToString(", ") { it.label }
        dialogBinding.tvSelectedSeats.text = "Ghế đã chọn: $seatLabels"

        val totalPrice = selectedSeats.sumOf { (it.ticket?.price ?: 0.0) + showtimePrice }
        dialogBinding.tvTotalPrice.text = "Tổng tiền: $totalPrice đ"

        dialogBinding.btnContinue.setOnClickListener {
            val ticketsSelected = ArrayList(selectedSeats.mapNotNull { it.ticket })

            ticketsSelected.forEach { ticket ->
                viewModel.updateTicketStatus(
                    showtimeId = showtimeId,
                    ticketId = ticket.ticketId,
                    status = "locked",
                    userId = FirebaseAuth.getInstance().currentUser?.uid
                )
            }

            val bundle = Bundle().apply {
                putInt("selectedSeatCount", selectedSeats.size)
                putDouble("totalPrice", totalPrice)
                putString("districtId", districtId)
                putString("showtimeId", showtimeId)
                putParcelableArrayList("selectedTickets", ticketsSelected)

            }

            val fragment = ChooseComboFragment().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack("ChooseComboFragment")
                .commit()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun hasInvalidGap(selectedSeats: List<Seat>, allSeats: List<Seat>): Boolean {
        val seatsByRow = allSeats.groupBy { it.row }

        for ((row, seatsInRow) in seatsByRow) {
            val selectedPositions =
                selectedSeats.filter { it.row == row }.map { it.position }.sorted()

            if (selectedPositions.isEmpty()) continue

            val minSelectedPos = selectedPositions.first()
            val maxSelectedPos = selectedPositions.last()

            for (pos in minSelectedPos..maxSelectedPos) {
                if (pos !in selectedPositions) {
                    Toast.makeText(
                        requireContext(),
                        "Không được bỏ trống ghế ở hàng $row vị trí $pos",
                        Toast.LENGTH_LONG
                    ).show()
                    return true
                }
            }
        }
        return false
    }

}
