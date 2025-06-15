package com.example.movieland.ui.features.home.roomandseat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.databinding.FragmentChooseSeatBinding
import com.example.movieland.ui.features.home.roomandseat.model.SeatLayout
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ChooseSeatFragment : BaseFragment<FragmentChooseSeatBinding>() {
    private var roomId: String = ""
    private var showtimeId: String = ""
    private lateinit var seatLayout: SeatLayout
    private lateinit var seatAdapter: SeatAdapter
    private val viewModel: ChooseSeatViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChooseSeatBinding {
        return FragmentChooseSeatBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.zoomLayout.setHasClickableChildren(true)

        seatAdapter = SeatAdapter(emptyList()) { seat, position ->
            Log.d("ChooseSeatFragment", "Clicked ${seat.label}, booked=${seat.ticket?.status}, price=${seat.ticket?.price}")
            if (!seat.isDummy && seat.ticket?.status != "booked") {
                seat.isSelected = !seat.isSelected
                seatAdapter.notifyItemChanged(position)
            }
        }

        binding.recyclerViewSeats.apply {
            layoutManager = GridLayoutManager(requireContext(), 8) // 8 là số cột tạm thời
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


    override fun setupClickView() {}

    private fun parseAndDisplayLayout(layoutJson: String, tickets: List<FirestoreTicket>) {
        seatLayout = Gson().fromJson(layoutJson, SeatLayout::class.java)

        seatLayout.seats.forEach { seat ->
            seat.ticket = tickets.find { it.seatLabel == seat.label }
        }

        // Cập nhật lại adapter với danh sách mới
        seatAdapter.updateSeats(seatLayout.seats)

        // Cập nhật lại số cột layout nếu cần
        (binding.recyclerViewSeats.layoutManager as? GridLayoutManager)?.spanCount = seatLayout.columns
    }

    private fun setupRecyclerView() {
        binding.recyclerViewSeats.apply {
            layoutManager = GridLayoutManager(requireContext(), seatLayout.columns)
            adapter = seatAdapter
            setHasFixedSize(true)
        }
    }


}
