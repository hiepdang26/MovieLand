package com.example.admin.ui.features.district.show

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.R
import com.example.admin.databinding.FragmentShowDistrictBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.district.add.AddDistrictFragment
import com.example.admin.ui.features.district.edit.EditDistrictFragment
import com.example.admin.ui.features.region.edit.EditRegionFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowDistrictFragment : BaseFragment<FragmentShowDistrictBinding>() {

    private val viewModel: ShowDistrictViewModel by viewModels()
    private lateinit var adapter: DistrictAdapter
    private var regionId: String? = null
    private var regionName: String? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentShowDistrictBinding {
        return FragmentShowDistrictBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        regionId = arguments?.getString("regionId")
        regionName = arguments?.getString("regionName")
        if (regionId == null) {
            Toast.makeText(requireContext(), "Region ID không hợp lệ", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.loadDistricts(regionId!!)
        }
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
        setupInitialData()
        setupClickView()
        setupObserver()
    }

    override fun setupInitialData() {
        adapter = DistrictAdapter()
        binding.rcvDistrict.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvDistrict.adapter = adapter
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.districts.collectLatest { districts ->
                adapter.submitList(districts)
            }
        }
        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { error ->
                error?.let {
                    Toast.makeText(requireContext(), "Lỗi: $it", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun setupClickView() {
        adapter.onItemClick = { district ->
            navigateToEditDistrict(district.regionId, district.id)
        }
        binding.btnAdd.setOnClickListener {
            val fragment = AddDistrictFragment().apply {
                arguments = Bundle().apply {
                    putString("regionName", regionName)
                    putString("regionIdSelected", regionId)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()

        }
    }

    private fun navigateToEditDistrict(regionId: String, districtId: String) {
        val fragment = EditDistrictFragment().apply {
            arguments = Bundle().apply {
                putString("regionId", regionId)
                putString("districtId", districtId)
                putString("regionName", regionName)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

}
