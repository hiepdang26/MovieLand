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
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.databinding.BottomSheetSelectedSeatsBinding
import com.example.movieland.databinding.FragmentChooseSeatBinding
import com.example.movieland.ui.features.home.roomandseat.model.Seat
import com.example.movieland.ui.features.home.roomandseat.model.SeatLayout
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ChooseSeatFragment : BaseFragment<FragmentChooseSeatBinding>() {
    private var roomId: String = ""
    private var showtimeId: String = ""
    private var movieName: String = ""
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

    override fun setupInitialData() {
        roomId = arguments?.getString("roomId") ?: return
        showtimeId = arguments?.getString("showtimeId") ?: return
        movieName = arguments?.getString("movieName") ?: return
        binding.txtNameMovie.text = movieName
        viewModel.loadRoomDetail(roomId)

    }


    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.room.collectLatest { room ->
                room?.let {
                    viewModel.loadTickets(showtimeId) { tickets ->
                        parseAndDisplayLayout(it.layoutJson, tickets)
                    }
                }
            }
        }
    }


    override fun setupClickView() {
        binding.btnContinue.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn ghế trước khi tiếp tục", Toast.LENGTH_SHORT).show()
            } else {
                showSelectedSeatsDialog()
            }
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun parseAndDisplayLayout(layoutJson: String, tickets: List<FirestoreTicket>) {
        seatLayout = Gson().fromJson(layoutJson, SeatLayout::class.java)

        seatLayout.seats.forEach { seat ->
            seat.ticket = tickets.find { it.seatLabel == seat.label }
        }
        seatAdapter.updateSeats(seatLayout.seats)
        (binding.recyclerViewSeats.layoutManager as? GridLayoutManager)?.spanCount =
            seatLayout.columns
    }

    private fun showSelectedSeatsDialog() {
        val dialogBinding = BottomSheetSelectedSeatsBinding.inflate(layoutInflater)
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val seatLabels = selectedSeats.joinToString(", ") { it.label }
        dialogBinding.tvSelectedSeats.text = "Ghế đã chọn: $seatLabels"

        val totalPrice = selectedSeats.sumOf { it.ticket?.price ?: 0.0 }
        dialogBinding.tvTotalPrice.text = "Tổng tiền: $totalPrice đ"

        dialogBinding.btnContinue.setOnClickListener {
        }

        dialog.show()
    }
}
