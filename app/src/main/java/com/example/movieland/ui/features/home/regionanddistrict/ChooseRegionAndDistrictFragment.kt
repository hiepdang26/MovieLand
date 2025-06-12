package com.example.movieland.ui.features.home.regionanddistrict

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.R
import com.example.movieland.databinding.FragmentChooseRegionAndDistrictBinding
import com.example.movieland.ui.features.home.showtime.ShowShowtimeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseRegionAndDistrictFragment : BaseFragment<FragmentChooseRegionAndDistrictBinding>() {

    private val viewModel: ChooseRegionAndDistrictViewModel by viewModels()
    private var regionAdapter: RegionAdapter? = null
    private var districtAdapter: DistrictAdapter? = null
    private var movieId: String = ""
    private var selectedDistrictId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieId = arguments?.getString("movieId").toString()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentChooseRegionAndDistrictBinding {
        return FragmentChooseRegionAndDistrictBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun setupInitialData() {
        regionAdapter = RegionAdapter {
            viewModel.onRegionSelected(it.id)
        }
        districtAdapter = DistrictAdapter { district ->
            selectedDistrictId = district.id
        }


        binding.recyclerRegion.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = regionAdapter
        }

        binding.recyclerDistrict.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = districtAdapter
        }
    }


    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.regions.collect {
                Log.d("ChooseFragment", "Regions size: ${it.size}")
                regionAdapter?.submitList(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.districts.collect {
                Log.d("ChooseFragment", "Districts size: ${it.size}")
                districtAdapter?.submitList(it)
            }
        }
    }

    override fun setupClickView() {
        binding.btnContinue.setOnClickListener {
            val districtId = selectedDistrictId ?: return@setOnClickListener

            val bundle = Bundle().apply {
                putString("districtId", districtId)
                putString("movieId", movieId)
            }

            val fragment = ShowShowtimeFragment()
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null).commit()
        }
    }
}