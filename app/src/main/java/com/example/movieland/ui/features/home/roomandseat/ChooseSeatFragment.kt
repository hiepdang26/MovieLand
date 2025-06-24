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
import com.example.movieland.R
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.databinding.BottomSheetSelectedSeatsBinding
import com.example.movieland.databinding.FragmentChooseSeatBinding
import com.example.movieland.ui.features.home.combo.ChooseComboFragment
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
                seat.isSelected = !seat.isSelected
                if (seat.isSelected) {
                    selectedSeats.add(seat)
                    Log.d("ChooseSeatFragment", "Chọn ghế: ${seat.label}, hàng: ${seat.row}, vị trí: ${seat.position}")
                    val seatCountInRow = selectedSeats.count { it.row == seat.row }
                    Log.d("ChooseSeatFragment", "Số ghế đã chọn trong hàng ${seat.row}: $seatCountInRow")
                    val ticket = seat.ticket
                    ticket?.let {

                    }
                } else {
                    selectedSeats.remove(seat)
                    Log.d("ChooseSeatFragment", "Bỏ chọn ghế: ${seat.label}, hàng: ${seat.row}, vị trí: ${seat.position}")
                    val seatCountInRow = selectedSeats.count { it.row == seat.row }
                    Log.d("ChooseSeatFragment", "Số ghế đã chọn trong hàng ${seat.row}: $seatCountInRow")
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
        districtId = arguments?.getString("districtId") ?: return
        showtimePrice = arguments?.getString("showtimePrice")?.toDoubleOrNull() ?: 0.0
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
            } else if (hasInvalidGap(selectedSeats, seatLayout.seats)) {
                // có gap không hợp lệ, nên không tiếp tục
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
            seat.position = seat.column - 1
        }

        seatAdapter.updateSeats(seatLayout.seats)
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
            Log.d("ChooseSeatFragment", "ticketsSelected: $ticketsSelected.")

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
                .addToBackStack(null)
                .commit()

            dialog.dismiss()
        }

        dialog.show()
    }
    private fun hasInvalidGap(selectedSeats: List<Seat>, allSeats: List<Seat>): Boolean {
        val seatsByRow = allSeats.groupBy { it.row }

        for ((row, seatsInRow) in seatsByRow) {
            val selectedPositions = selectedSeats.filter { it.row == row }.map { it.position }.sorted()

            if (selectedPositions.isEmpty()) continue

            val minSelectedPos = selectedPositions.first()
            val maxSelectedPos = selectedPositions.last()

            // Kiểm tra khoảng trống giữa min và max vị trí ghế được chọn
            for (pos in minSelectedPos..maxSelectedPos) {
                if (pos !in selectedPositions) {
                    Toast.makeText(requireContext(),
                        "Không được bỏ trống ghế ở hàng $row vị trí $pos",
                        Toast.LENGTH_LONG).show()
                    return true
                }
            }
        }
        return false
    }

}
