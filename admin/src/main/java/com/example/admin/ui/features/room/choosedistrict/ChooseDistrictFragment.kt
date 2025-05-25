package com.example.admin.ui.features.room.choosedistrict

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
import com.example.admin.databinding.FragmentChooseDistrictBinding
import com.example.admin.databinding.FragmentEditRoomBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.room.show.ShowRoomFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ChooseDistrictFragment : BaseFragment<FragmentChooseDistrictBinding>() {
    private val viewModel: ChooseDistrictViewModel by viewModels()
    private lateinit var adapter: ChooseDistrictAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChooseDistrictBinding {
        return FragmentChooseDistrictBinding.inflate(layoutInflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        setupObserver()
    }

    override fun setupInitialData() {
        viewModel.loadDistricts()
    }


    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.districts.collectLatest { list ->
                adapter = ChooseDistrictAdapter(list) { district ->
                    val bundle = Bundle().apply {
                        putString("districtId", district.id)
                        putString("districtName", district.name)
                    }
                    val showRoomFragment = ShowRoomFragment().apply {
                        arguments = bundle
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, showRoomFragment)
                        .addToBackStack(null)
                        .commit()
                }

                binding.rcvDistrict.layoutManager = LinearLayoutManager(requireContext())
                binding.rcvDistrict.adapter = adapter
            }
        }
    }


    override fun setupClickView() {
    }
}