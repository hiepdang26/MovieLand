package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.district

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.databinding.FragmentChooseDistrictForTicketBinding
import com.example.admin.databinding.FragmentShowTicketBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.showtimes.choosedistrictandroom.district.ChooseDistrictForRoomAdapter
import com.example.admin.ui.features.showtimes.choosedistrictandroom.room.ChooseRoomFragment
import com.example.admin.ui.features.ticket.add.AddTicketFragment
import com.example.admin.ui.features.ticket.add.AddTicketViewModel
import com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.room.ChooseRoomForTicketFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseDistrictForTicketFragment : BaseFragment<FragmentChooseDistrictForTicketBinding>() {

    private lateinit var adapter: ChooseDistrictForTicketAdapter

    private val viewModel: ChooseDistrictForTicketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentChooseDistrictForTicketBinding {
        return FragmentChooseDistrictForTicketBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).showNavigationBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupRecyclerView()
        setupObserver()
    }


    private fun setupRecyclerView() {
        adapter = ChooseDistrictForTicketAdapter(ArrayList()) { district ->
            val bundle = Bundle().apply { putString("districtId", district.id) }
            val showRoomFragment = ChooseRoomForTicketFragment().apply { arguments = bundle }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, showRoomFragment).addToBackStack(null).commit()
        }

        binding.rcvDistrict.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvDistrict.adapter = adapter
    }

    override fun setupInitialData() {
        viewModel.loadDistricts()
    }


    override fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.districts.collectLatest { list ->
                adapter.updateData(list)
            }
        }
    }


    override fun setupClickView() {
    }
}