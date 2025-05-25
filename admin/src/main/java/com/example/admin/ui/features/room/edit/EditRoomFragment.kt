package com.example.admin.ui.features.room.edit

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.databinding.FragmentEditRoomBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.room.layoutseat.edit.EditLayoutSeatFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditRoomFragment : BaseFragment<FragmentEditRoomBinding>() {
    private val viewModel: EditRoomViewModel by viewModels()

    private var roomId: String = ""
    private var layoutJson: String = ""
    private var totalSeat: Int = 0
    private var seatInEachRow: Int = 0

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEditRoomBinding.inflate(inflater, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomId = arguments?.getString("roomId") ?: ""
        if (roomId.isNotEmpty()) {
            viewModel.loadRoom(roomId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupClickView()


    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
        parentFragmentManager.setFragmentResultListener(
            "requestKey_editLayout", viewLifecycleOwner
        ) { _, bundle ->
            layoutJson = bundle.getString("layoutJson") ?: ""
            totalSeat = bundle.getInt("totalSeat")
            seatInEachRow = bundle.getInt("seatInEachRow")
            Log.d("EditRoomFragment", "Nhận layout sửa từ EditLayoutSeatFragment: $layoutJson")
        }
        binding.edtTotalSeat.setText(totalSeat.toString())
        binding.edtSeatInEachRow.setText(seatInEachRow.toString())
    }

    override fun setupInitialData() {
        TODO("Not yet implemented")
    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.roomDetail.collect { room ->
                room?.let {
                    binding.txtDistrictName.setText(it.districtName)
                    binding.edtRoomName.setText(it.name)
                    binding.edtTotalSeat.setText(it.totalSeats.toString())
                    binding.edtSeatInEachRow.setText(it.seatInRow.toString())
                    layoutJson = it.layoutJson
                    totalSeat = it.totalSeats
                    seatInEachRow = it.seatInRow
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.saveResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(
                            requireContext(), "Cập nhật phòng thành công", Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetSaveResult()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Xóa phòng thành công", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Lỗi khi xóa phòng: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.resetDeleteResult()
                }
            }
        }
    }

    override fun setupClickView() {
        binding.btnDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xóa phòng này không?")
                .setPositiveButton("Xóa") { dialog, _ ->
                    viewModel.deleteRoom(roomId)
                    dialog.dismiss()
                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.btnEditLayout.setOnClickListener {
            val totalSeatText = binding.edtTotalSeat.text.toString().trim()
            val seatInEachRowText = binding.edtSeatInEachRow.text.toString().trim()

            val newTotalSeat = totalSeatText.toIntOrNull()
            val newSeatInEachRow = seatInEachRowText.toIntOrNull()

            binding.edtTotalSeat.error = null
            binding.edtSeatInEachRow.error = null

            if (newTotalSeat == null || newTotalSeat < 2) {
                binding.edtTotalSeat.error = "Tổng số ghế phải là số nguyên ≥ 2"
                return@setOnClickListener
            }

            if (newSeatInEachRow == null || newSeatInEachRow < 2) {
                binding.edtSeatInEachRow.error = "Số ghế mỗi hàng phải là số nguyên ≥ 2"
                return@setOnClickListener
            }

            if (newTotalSeat % newSeatInEachRow != 0) {
                binding.edtSeatInEachRow.error = "Tổng số ghế phải chia hết cho số ghế mỗi hàng"
                return@setOnClickListener
            }

            val useNewLayout = (newTotalSeat != totalSeat) || (newSeatInEachRow != seatInEachRow)

            totalSeat = newTotalSeat
            seatInEachRow = newSeatInEachRow

            binding.edtTotalSeat.setText(totalSeat.toString())
            binding.edtSeatInEachRow.setText(seatInEachRow.toString())

            val fragment = EditLayoutSeatFragment().apply {
                arguments = Bundle().apply {
                    putInt("totalSeat", totalSeat)
                    putInt("seatInEachRow", seatInEachRow)
                    putString("layoutJson", if (useNewLayout) "" else layoutJson)
                }
            }
            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null).commit()
        }

        binding.btnSaveRoom.setOnClickListener {
            val name = binding.edtRoomName.text.toString().trim()
            val totalSeatText = binding.edtTotalSeat.text.toString().trim()
            val seatInEachRowText = binding.edtSeatInEachRow.text.toString().trim()

            val totalSeat = totalSeatText.toIntOrNull() ?: 0
            val seatInEachRow = seatInEachRowText.toIntOrNull() ?: 0

            viewModel.saveRoom(roomId, name, totalSeat, seatInEachRow, layoutJson)
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
