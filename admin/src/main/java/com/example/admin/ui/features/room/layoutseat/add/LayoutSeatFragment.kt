package com.example.admin.ui.features.room.layoutseat.add

import SeatSelectionTouchListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.databinding.FragmentLayoutSeatBinding
import com.example.admin.ui.features.room.layoutseat.SeatAdapter
import com.example.admin.ui.features.room.layoutseat.SpaceItemDecoration
import com.example.admin.ui.features.room.layoutseat.model.Seat
import org.json.JSONArray
import org.json.JSONObject

class LayoutSeatFragment : Fragment() {
    private var selectedRangePositions: Set<Int> = emptySet()

    private lateinit var binding: FragmentLayoutSeatBinding
    private lateinit var seatAdapter: SeatAdapter

    private var seatList: MutableList<Seat> = mutableListOf()
    private var currentSeatType: Seat.SeatType = Seat.SeatType.NORMAL

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLayoutSeatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val totalSeat = arguments?.getInt("totalSeat") ?: 0
        val seatInEachRow = arguments?.getInt("seatInEachRow") ?: 0

        val rows = if (seatInEachRow > 0) totalSeat / seatInEachRow else 0

        seatList = createSeats(rows, seatInEachRow)

        seatAdapter = SeatAdapter(seatList) { seat, position ->
            onSeatClicked(seat, position)
        }

        binding.recyclerViewSeats.apply {
            layoutManager = GridLayoutManager(requireContext(), seatInEachRow)
            adapter = seatAdapter
            addItemDecoration(SpaceItemDecoration(8))

            addOnItemTouchListener(
                SeatSelectionTouchListener(this) { selectedPositions ->
                    selectedRangePositions = selectedPositions
                    selectedPositions.forEach { pos ->
                        if (pos in seatList.indices) {
                            seatList[pos].isSelected = true
                            seatList[pos].type = currentSeatType
                        }
                    }
                    seatAdapter.notifyDataSetChanged()
                }
            )
        }

        setupButtons()
    }



    private fun createSeats(rows: Int, columns: Int): MutableList<Seat> {
        val seats = mutableListOf<Seat>()
        for (i in 0 until rows) {
            val rowChar = ('A' + i)
            for (j in 0 until columns) {
                val label = "$rowChar${j + 1}"
                seats.add(Seat(row = i + 1, column = j + 1, label = label))
            }
        }
        return seats
    }


    private fun onSeatClicked(seat: Seat, position: Int) {
        if (seat.isSelected) {
            seat.isSelected = false
            seat.type = Seat.SeatType.NORMAL
        } else {
            seat.isSelected = true
            seat.type = currentSeatType
        }
        seatAdapter.notifyItemChanged(position)
    }

    private fun setupButtons() {
        binding.btnNormal.setOnClickListener {
            currentSeatType = Seat.SeatType.NORMAL
            updateButtonStates()
        }
        binding.btnVIP.setOnClickListener {
            currentSeatType = Seat.SeatType.VIP
            updateButtonStates()
        }

        binding.btnSave.setOnClickListener {
            val layoutJson = generateLayoutJson()
            Log.d("LayoutSeatFragment", "Layout JSON tạo được: $layoutJson")

            parentFragmentManager.setFragmentResult(
                "requestKey_roomLayout",
                Bundle().apply {
                    putString("layoutJson", layoutJson)
                }
            )

            parentFragmentManager.popBackStack()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun generateLayoutJson(): String {
        val json = JSONObject()
        val rows = seatList.maxOfOrNull { it.row } ?: 0
        val columns = seatList.maxOfOrNull { it.column } ?: 0

        json.put("rows", rows)
        json.put("columns", columns)

        val seatsJsonArray = JSONArray()
        for (seat in seatList) {
            val seatJson = JSONObject()
            seatJson.put("row", seat.row)
            seatJson.put("column", seat.column)
            seatJson.put("label", seat.label)
            seatJson.put("type", seat.type.name)
            seatsJsonArray.put(seatJson)
        }
        json.put("seats", seatsJsonArray)

        return json.toString()
    }

    private fun updateButtonStates() {
        binding.btnNormal.setBackgroundResource(
            if (currentSeatType == Seat.SeatType.NORMAL) R.drawable.selector_button_normal_selected
            else R.drawable.selector_button_normal_unselected
        )
        binding.btnVIP.setBackgroundResource(
            if (currentSeatType == Seat.SeatType.VIP) R.drawable.selector_button_vip_selected
            else R.drawable.selector_button_vip_unselected
        )
    }
}