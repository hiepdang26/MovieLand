package com.example.movieland.ui.features.home.regionanddistrict

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.MainActivity
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
    private var movieName: String = ""
    private var selectedDistrictId: String = ""
    private var selectedDistrictName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieId = arguments?.getString("movieId").toString()
        movieName = arguments?.getString("movieName").toString()
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
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()

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
            selectedDistrictName = district.name
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
                selectedDistrictId = ""
                selectedDistrictName = ""
                districtAdapter?.submitList(it)
            }
        }
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnContinue.setOnClickListener {
            if (selectedDistrictId.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn khu vực trước khi tiếp tục", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bundle = Bundle().apply {
                putString("districtId", selectedDistrictId)
                putString("districtName", selectedDistrictName)
                putString("movieId", movieId)
                putString("movieName", movieName)
            }
            val fragment = ShowShowtimeFragment()
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}