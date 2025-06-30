package com.example.admin.ui.features.showtimes.choosedistrictandroom.room

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.databinding.FragmentChooseRoomBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import android.widget.Toast
import androidx.constraintlayout.helper.widget.Grid
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.admin.R
import com.example.admin.ui.features.room.add.AddRoomFragment
import com.example.admin.ui.features.room.show.ShowRoomFragment
import com.example.admin.ui.features.showtimes.show.ShowShowtimeFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseRoomFragment : BaseFragment<FragmentChooseRoomBinding>() {

    private var districtId: String = ""
    private lateinit var adapter: ChooseRoomAdapter

    private val viewModel: ChooseRoomViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentChooseRoomBinding {
        return FragmentChooseRoomBinding.inflate(layoutInflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        districtId = arguments?.getString("districtId") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun setupInitialData() {
        if (districtId.isNotEmpty()) {
            viewModel.loadRoomsByDistrictId(districtId)
        } else {
            Toast.makeText(requireContext(), "Không có districtId hợp lệ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rooms.collectLatest { rooms ->
                    adapter = ChooseRoomAdapter(rooms) { room ->
                        val fragment = ShowShowtimeFragment().apply {
                            arguments = Bundle().apply {
                                putString("roomId", room.id)
                                putString("roomName", room.name)
                                putString("districtId", room.districtId)
                                putString("districtName", room.districtName)
                                Log.d("ChooseRoomFragment", "setupObserver: ${room.totalSeats}, ${room.seatInRow}")
                                putString("totalSeat", room.totalSeats.toString())
                                putString("seatInEachRow", room.seatInRow.toString())
                            }
                        }

                        parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
                            .addToBackStack(null).commit()
                    }
                    binding.rcvRoom.layoutManager = GridLayoutManager(requireContext(), 2)
                    binding.rcvRoom.adapter = adapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collectLatest { errorMsg ->
                    errorMsg?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
