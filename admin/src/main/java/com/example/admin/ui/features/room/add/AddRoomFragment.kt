package com.example.admin.ui.features.room.add

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
import com.example.admin.databinding.FragmentAddRoomBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.room.layoutseat.add.LayoutSeatFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddRoomFragment : BaseFragment<FragmentAddRoomBinding>() {
    private val viewModel: AddRoomViewModel by viewModels()

    private var layoutJson: String? = null
    private var districtId: String = ""
    private var districtName: String = ""

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAddRoomBinding.inflate(inflater, container, false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun setupInitialData() {
        districtId = arguments?.getString("districtId") ?: ""
        districtName = arguments?.getString("districtName") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupClickView()
        setupObserver()
    }

    override fun setupClickView() {
        binding.txtDistrictName.text = districtName
        binding.btnEditLayout.setOnClickListener {
            val totalSeatText = binding.edtTotalSeat.text.toString().trim()
            val seatInEachRowText = binding.edtSeatInEachRow.text.toString().trim()

            val totalSeat = totalSeatText.toIntOrNull()
            val seatInEachRow = seatInEachRowText.toIntOrNull()

            binding.edtTotalSeat.error = null
            binding.edtSeatInEachRow.error = null

            if (totalSeat == null) {
                binding.edtTotalSeat.error = "Vui lòng nhập số hợp lệ"
                return@setOnClickListener
            }

            if (seatInEachRow == null) {
                binding.edtSeatInEachRow.error = "Vui lòng nhập số hợp lệ"
                return@setOnClickListener
            }

            if (totalSeat < 2) {
                binding.edtTotalSeat.error = "Tổng số ghế phải lớn hơn hoặc bằng 2"
                return@setOnClickListener
            }

            if (seatInEachRow < 2) {
                binding.edtSeatInEachRow.error = "Số ghế mỗi hàng phải lớn hơn hoặc bằng 2"
                return@setOnClickListener
            }

            if (seatInEachRow > totalSeat) {
                binding.edtSeatInEachRow.error = "Số ghế mỗi hàng không được lớn hơn tổng số ghế"
                return@setOnClickListener
            }

            if (totalSeat % seatInEachRow != 0) {
                binding.edtSeatInEachRow.error = "Số ghế mỗi hàng phải là ước số của tổng số ghế"
                return@setOnClickListener
            }

            val fragment = LayoutSeatFragment().apply {
                arguments = Bundle().apply {
                    putInt("totalSeat", totalSeat)
                    putInt("seatInEachRow", seatInEachRow)
                }
            }
            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null).commit()
        }

        binding.btnSaveRoom.setOnClickListener {
            val name = binding.edtRoomName.text.toString().trim()
            val totalSeatText = binding.edtTotalSeat.text.toString().trim()
            val totalSeat = totalSeatText.toIntOrNull() ?: 0

            val seatInEachRowText = binding.edtSeatInEachRow.text.toString().trim()
            val seatInEachRow = seatInEachRowText.toIntOrNull() ?: 0

            val districtId = arguments?.getString("districtId")
                ?: ""
            val layout = layoutJson ?: ""

            viewModel.saveRoom(districtId,districtName,  name, totalSeat, seatInEachRow, layout)
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.saveResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Lưu phòng thành công", Toast.LENGTH_SHORT)
                            .show()
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
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
        parentFragmentManager.setFragmentResultListener(
            "requestKey_roomLayout", viewLifecycleOwner
        ) { _, bundle ->
            layoutJson = bundle.getString("layoutJson")
            Log.d("AddRoomFragment", "Nhận layout JSON: $layoutJson")
        }
    }

}
