package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.room

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.databinding.FragmentChooseDistrictForTicketBinding
import com.example.admin.databinding.FragmentChooseRoomForTicketBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.showtimes.choosedistrictandroom.room.ChooseRoomAdapter
import com.example.admin.ui.features.showtimes.choosedistrictandroom.room.ChooseRoomFragment
import com.example.admin.ui.features.showtimes.show.ShowShowtimeFragment
import com.example.admin.ui.features.ticket.add.AddTicketFragment
import com.example.admin.ui.features.ticket.add.AddTicketViewModel
import com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.district.ChooseDistrictForTicketAdapter
import com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.showtime.ChooseShowtimeForTicketFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseRoomForTicketFragment : BaseFragment<FragmentChooseRoomForTicketBinding>() {
    private var districtId: String = ""
    private lateinit var adapter: ChooseRoomForTicketAdapter

    private val viewModel: ChooseRoomForTicketViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentChooseRoomForTicketBinding {
        return FragmentChooseRoomForTicketBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =  getViewBinding(inflater, container)
        return binding.root
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
        lifecycleScope.launch {
            viewModel.rooms.collectLatest { rooms ->
                adapter = ChooseRoomForTicketAdapter(rooms) { room ->
                    val fragment = ChooseShowtimeForTicketFragment().apply {
                        arguments = Bundle().apply {
                            putString("roomId", room.id)
                            putString("districtId", room.districtId)
                        }
                    }

                    parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
                        .addToBackStack(null).commit()

                }
                binding.rcvRoom.layoutManager = LinearLayoutManager(requireContext())
                binding.rcvRoom.adapter = adapter
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { errorMsg ->
                errorMsg?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
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