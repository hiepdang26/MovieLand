package com.example.admin.ui.features.room.layoutseat.edit

import SeatSelectionTouchListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.databinding.FragmentEditLayoutSeatBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.room.layoutseat.SeatAdapter
import com.example.admin.ui.features.room.layoutseat.SpaceItemDecoration
import com.example.admin.ui.features.room.layoutseat.model.Seat
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
@AndroidEntryPoint
class EditLayoutSeatFragment : BaseFragment<FragmentEditLayoutSeatBinding>() {

    private lateinit var seatList: MutableList<Seat>
    private lateinit var seatAdapter: SeatAdapter

    private var totalSeat: Int = 0
    private var seatInEachRow: Int = 0
    private var layoutJson: String = ""

    private var currentSeatType: Seat.SeatType = Seat.SeatType.NORMAL
    private var selectedRangePositions: Set<Int> = emptySet()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEditLayoutSeatBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupClickView()
    }

    override fun setupInitialData() {
        totalSeat = arguments?.getInt("totalSeat") ?: 0
        seatInEachRow = arguments?.getInt("seatInEachRow") ?: 0
        layoutJson = arguments?.getString("layoutJson") ?: ""

        seatList = if (layoutJson.isNotBlank()) {
            parseLayoutJson(layoutJson)
        } else {
            createSeats(totalSeat / seatInEachRow, seatInEachRow)
        }

        seatAdapter = SeatAdapter(seatList) { seat, position ->
            onSeatClicked(seat, position)
        }
    }

    override fun setupObserver() {
        TODO("Not yet implemented")
    }

    override fun setupClickView() {
        binding.recyclerViewSeats.apply {
            layoutManager = GridLayoutManager(requireContext(), seatInEachRow)
            adapter = seatAdapter
            addItemDecoration(SpaceItemDecoration(8))

            addOnItemTouchListener(
                SeatSelectionTouchListener(this) { selectedPositions ->
                    selectedRangePositions = selectedPositions
                    // Kéo chọn: set isSelected và type cho từng ghế được kéo chọn
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

        binding.btnNormal.setOnClickListener {
            currentSeatType = Seat.SeatType.NORMAL
            updateButtonStates()
        }

        binding.btnVIP.setOnClickListener {
            currentSeatType = Seat.SeatType.VIP
            updateButtonStates()
        }

        binding.btnSave.setOnClickListener {
            val updatedLayoutJson = generateLayoutJson(seatList)
            val bundle = Bundle().apply {
                putString("layoutJson", updatedLayoutJson)
                putInt("totalSeat", totalSeat)
                putInt("seatInEachRow", seatInEachRow)
            }
            parentFragmentManager.setFragmentResult("requestKey_editLayout", bundle)
            parentFragmentManager.popBackStack()
        }

        updateButtonStates()

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
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

    private fun parseLayoutJson(json: String): MutableList<Seat> {
        val seats = mutableListOf<Seat>()
        try {
            val jsonObj = JSONObject(json)
            val seatsArray = jsonObj.getJSONArray("seats")
            for (i in 0 until seatsArray.length()) {
                val seatObj = seatsArray.getJSONObject(i)
                val row = seatObj.getInt("row")
                val column = seatObj.getInt("column")
                val label = seatObj.getString("label")
                val typeName = seatObj.getString("type")
                val type = Seat.SeatType.valueOf(typeName)
                seats.add(Seat(row, column, label, type, true))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return seats
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

    private fun generateLayoutJson(seats: List<Seat>): String {
        val json = JSONObject()
        val rows = seats.maxOfOrNull { it.row } ?: 0
        val columns = seats.maxOfOrNull { it.column } ?: 0

        json.put("rows", rows)
        json.put("columns", columns)

        val seatsJsonArray = JSONArray()
        seats.forEach { seat ->
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

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
    }
}
