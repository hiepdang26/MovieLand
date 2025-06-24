package com.example.admin.ui.features.room.show

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.databinding.FragmentShowRoomBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.district.show.ShowDistrictFragment
import com.example.admin.ui.features.room.add.AddRoomFragment
import com.example.admin.ui.features.room.edit.EditRoomFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowRoomFragment : BaseFragment<FragmentShowRoomBinding>() {
    private val viewModel: ShowRoomViewModel by viewModels()
    private var districtId: String = ""
    private var districtName: String = ""
    private lateinit var adapter: ShowRoomAdapter

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentShowRoomBinding {
        return FragmentShowRoomBinding.inflate(layoutInflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ShowRoomAdapter { room ->
            val fragment = EditRoomFragment().apply {
                arguments = Bundle().apply {
                    putString("roomId", room.id)
                }
            }
            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null).commit()
        }

        binding.rcvRoom.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvRoom.adapter = adapter

        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun setupInitialData() {
        districtId = arguments?.getString("districtId") ?: ""
        districtName = arguments?.getString("districtName") ?: ""
        Log.d("ShowRoomFragment", districtName)
        if (districtId.isNotEmpty()) {
            viewModel.loadRoomsByDistrictId(districtId)
        } else {
        }
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.rooms.collectLatest { rooms ->
                adapter.submitList(rooms)
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { errorMsg ->
                errorMsg?.let {

                }
            }
        }
    }

    override fun setupClickView() {
        binding.btnAdd.setOnClickListener {
            val fragment = AddRoomFragment().apply {
                arguments = Bundle().apply {
                    putString("districtId", districtId)
                    putString("districtName", districtName)
                }
            }

            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null).commit()
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).showNavigationBar()
    }


}
